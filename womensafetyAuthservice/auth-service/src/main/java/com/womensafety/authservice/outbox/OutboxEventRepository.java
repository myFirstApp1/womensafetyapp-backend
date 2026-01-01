package com.womensafety.authservice.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    boolean existsByEventId(String eventId);

    /**
     * Fetch a small batch of unpublished rows, locking them to this publisher worker.
     * Uses SKIP LOCKED so multiple instances can safely run in parallel.
     *
     * NOTE: Works on MySQL 8+ with InnoDB.
     */
    @Query(
            value = """
            SELECT *
            FROM outbox_events
            WHERE published_at IS NULL
            ORDER BY id
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
            """,
            nativeQuery = true
    )
    List<OutboxEvent> fetchBatchForPublish(@Param("batchSize") int batchSize);

    @Query(
            value = """
            SELECT COALESCE(MIN(created_at), '1970-01-01 00:00:01') 
            FROM outbox_events 
            WHERE published_at IS NULL
            """,
            nativeQuery = true
    )
    Instant oldestUnpublishedCreatedAt();
}
