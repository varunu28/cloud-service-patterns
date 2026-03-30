package com.varunu28.orderservice.repository;

import com.varunu28.orderservice.model.Outbox;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends CrudRepository<Outbox, Long> {

    @Query(value = """
        SELECT o.id as id,
               o.event_type as eventType,
               o.payload as payload,
               o.retry_count as retryCount,
               o.next_retry_at as nextRetryAt
        FROM outbox o
        WHERE o.state = 'PENDING' AND (o.next_retry_at IS NULL OR o.next_retry_at <= NOW())
        LIMIT 10
        """, nativeQuery = true)
    List<OutboxProjection> findAllPending();

    @Modifying
    @Query(value = "UPDATE outbox SET state = 'PROCESSED' WHERE id IN (:ids)", nativeQuery = true)
    void markAsProcessed(@Param("ids") List<Long> ids);

    @Modifying
    @Query(value = "UPDATE outbox SET state = 'FAILED' WHERE id IN (:ids)", nativeQuery = true)
    void markAsFailed(@Param("ids") List<Long> ids);

    @Modifying
    @Query(value = """
        UPDATE outbox
        SET retry_count = retry_count + 1,
            next_retry_at = NOW() + (retry_count + 1) * INTERVAL '30 seconds'
        WHERE id IN (:ids)
        """, nativeQuery = true)
    void incrementRetry(@Param("ids") List<Long> ids);
}
