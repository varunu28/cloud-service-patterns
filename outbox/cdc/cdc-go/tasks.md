# pglogrepl + Kafka Integration Checklist

## Postgres Setup
- [ ] Enable logical replication in Postgres (`wal_level = logical`)
- [ ] Create a publication for the outbox table (`CREATE PUBLICATION outbox_pub FOR TABLE outbox`)

## Go Setup
- [ ] Add dependencies — `github.com/jackc/pglogrepl` and `github.com/jackc/pgconn`
- [ ] Add Kafka client dependency — `github.com/segmentio/kafka-go` or `github.com/confluentinc/confluent-kafka-go`

## Replication Connection
- [ ] Open a replication connection using `pgconn` with `replication=database` parameter
- [ ] Create a logical replication slot with `pgoutput` plugin using `pglogrepl.CreateReplicationSlot`
- [ ] Start replication stream using `pglogrepl.StartReplication` with publication name

## Streaming Loop
- [ ] Read messages using `pgconn.ReceiveMessage` in a loop
- [ ] Handle `pglogrepl.PrimaryKeepaliveMessage` — respond with standby status to avoid timeout
- [ ] Handle `pglogrepl.XLogDataMsg` — this contains your actual WAL changes
- [ ] Parse `XLogDataMsg` using `pglogrepl.ParseV2` to get structured change events
- [ ] Filter for `InsertMessage` on your outbox table specifically

## Kafka Publishing
- [ ] Extract `event_type` and `payload` from the parsed insert message
- [ ] Publish to Kafka topic with appropriate partition key
- [ ] Update LSN (Log Sequence Number) position after successful publish using `pglogrepl.SendStandbyStatusUpdate`

## LSN Management
- [ ] Persist the last confirmed LSN so on restart you resume from where you left off rather than replaying from the beginning
