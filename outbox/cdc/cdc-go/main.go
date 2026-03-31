package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/jackc/pglogrepl"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgproto3"
	"github.com/segmentio/kafka-go"
)

const (
	slotName        = "outbox_slot"
	publicationName = "outbox_pub"
	kafkaBroker     = "localhost:9092"
	kafkaTopic      = "order-created"
	lsnFile         = "last_lsn.dat"
)

func main() {
	ctx, cancel := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer cancel()

	conn, err := pgconn.Connect(ctx, "postgres://order-user:secret@localhost:5432/order-service?replication=database")
	if err != nil {
		log.Fatalf("failed to connect: %v", err)
	}
	defer conn.Close(ctx)
	log.Println("connected to postgres with replication protocol")

	// Load persisted LSN or start fresh
	startLSN := loadLSN()

	// Create replication slot if it doesn't exist
	_, err = pglogrepl.CreateReplicationSlot(ctx, conn, slotName, "pgoutput",
		pglogrepl.CreateReplicationSlotOptions{Temporary: false})
	if err != nil {
		// Slot already exists is fine
		if pgErr, ok := err.(*pgconn.PgError); ok && pgErr.Code == "42710" {
			log.Println("replication slot already exists, reusing")
		} else {
			log.Fatalf("failed to create replication slot: %v", err)
		}
	} else {
		log.Println("created replication slot:", slotName)
	}

	// Start replication
	err = pglogrepl.StartReplication(ctx, conn, slotName, startLSN,
		pglogrepl.StartReplicationOptions{
			PluginArgs: []string{
				"proto_version '2'",
				fmt.Sprintf("publication_names '%s'", publicationName),
			},
		})
	if err != nil {
		log.Fatalf("failed to start replication: %v", err)
	}
	log.Println("replication stream started")

	kafkaWriter := &kafka.Writer{
		Addr:         kafka.TCP(kafkaBroker),
		Topic:        kafkaTopic,
		Balancer:     &kafka.LeastBytes{},
		RequiredAcks: kafka.RequireAll,
		BatchTimeout: 10 * time.Millisecond,
	}
	defer kafkaWriter.Close()

	standbyDeadline := time.Now().Add(10 * time.Second)
	var relations map[uint32]*pglogrepl.RelationMessageV2
	relations = make(map[uint32]*pglogrepl.RelationMessageV2)

	for {
		if ctx.Err() != nil {
			log.Println("shutting down")
			return
		}

		// Send standby status periodically to avoid timeout
		if time.Now().After(standbyDeadline) {
			err = pglogrepl.SendStandbyStatusUpdate(ctx, conn,
				pglogrepl.StandbyStatusUpdate{WALWritePosition: startLSN})
			if err != nil {
				log.Fatalf("failed to send standby status: %v", err)
			}
			standbyDeadline = time.Now().Add(10 * time.Second)
		}

		rawMsg, err := conn.ReceiveMessage(ctx)
		if err != nil {
			if ctx.Err() != nil {
				return
			}
			log.Fatalf("failed to receive message: %v", err)
		}

		if errMsg, ok := rawMsg.(*pgproto3.ErrorResponse); ok {
			log.Fatalf("received postgres error: %+v", errMsg)
		}

		copyData, ok := rawMsg.(*pgproto3.CopyData)
		if !ok {
			continue
		}

		switch copyData.Data[0] {
		case pglogrepl.PrimaryKeepaliveMessageByteID:
			pkm, err := pglogrepl.ParsePrimaryKeepaliveMessage(copyData.Data[1:])
			if err != nil {
				log.Fatalf("failed to parse keepalive: %v", err)
			}
			if pkm.ReplyRequested {
				standbyDeadline = time.Time{} // force immediate reply
			}

		case pglogrepl.XLogDataByteID:
			xld, err := pglogrepl.ParseXLogData(copyData.Data[1:])
			if err != nil {
				log.Fatalf("failed to parse XLogData: %v", err)
			}

			msg, err := pglogrepl.ParseV2(xld.WALData, false)
			if err != nil {
				log.Fatalf("failed to parse logical message: %v", err)
			}

			switch m := msg.(type) {
			case *pglogrepl.RelationMessageV2:
				relations[m.RelationID] = m

			case *pglogrepl.InsertMessageV2:
				rel, ok := relations[m.RelationID]
				if !ok {
					log.Printf("unknown relation ID %d, skipping", m.RelationID)
					continue
				}
				if rel.RelationName != "outbox" {
					continue
				}

				values := parseColumns(rel.Columns, m.Tuple.Columns)
				eventType := values["event_type"]
				payload := values["payload"]

				log.Printf("captured outbox event: type=%s payload=%s", eventType, payload)

				err = kafkaWriter.WriteMessages(ctx, kafka.Message{
					Key:   []byte(eventType),
					Value: []byte(payload),
				})
				if err != nil {
					log.Printf("failed to publish to kafka: %v", err)
					continue
				}
				log.Printf("published to kafka topic=%s key=%s", kafkaTopic, eventType)
			}

			// Advance LSN after processing
			newLSN := xld.WALStart + pglogrepl.LSN(len(xld.WALData))
			if newLSN > startLSN {
				startLSN = newLSN
				persistLSN(startLSN)
			}

			err = pglogrepl.SendStandbyStatusUpdate(ctx, conn,
				pglogrepl.StandbyStatusUpdate{WALWritePosition: startLSN})
			if err != nil {
				log.Fatalf("failed to send standby status: %v", err)
			}
			standbyDeadline = time.Now().Add(10 * time.Second)
		}
	}
}

func parseColumns(relColumns []*pglogrepl.RelationMessageColumn, tupleColumns []*pglogrepl.TupleDataColumn) map[string]string {
	values := make(map[string]string)
	for i, col := range tupleColumns {
		if i >= len(relColumns) {
			break
		}
		colName := relColumns[i].Name
		switch col.DataType {
		case 't': // text
			values[colName] = string(col.Data)
		case 'n': // null
			values[colName] = ""
		}
	}
	return values
}

func loadLSN() pglogrepl.LSN {
	data, err := os.ReadFile(lsnFile)
	if err != nil {
		log.Println("no persisted LSN found, starting from 0")
		return 0
	}
	lsn, err := pglogrepl.ParseLSN(string(data))
	if err != nil {
		log.Printf("failed to parse persisted LSN, starting from 0: %v", err)
		return 0
	}
	log.Printf("resuming from persisted LSN: %s", lsn)
	return lsn
}

func persistLSN(lsn pglogrepl.LSN) {
	err := os.WriteFile(lsnFile, []byte(lsn.String()), 0644)
	if err != nil {
		log.Printf("failed to persist LSN: %v", err)
	}
}
