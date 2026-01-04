-- ==== OUTBOX (transactional publisher for events) ====
CREATE TABLE outbox_events (
    id BINARY(16) NOT NULL,                    -- internal PK (UUID)
    event_id BINARY(16) NOT NULL,              -- public event UUID
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id BINARY(16) NOT NULL,           -- user_id UUID
    event_type VARCHAR(64) NOT NULL,
    payload JSON NOT NULL,
    headers_json JSON NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    published_at TIMESTAMP(6) NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_outbox_event_id (event_id),
    KEY idx_outbox_unpublished (published_at),
    KEY idx_outbox_aggregate (aggregate_type, aggregate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==== OTP CHALLENGES (verification) ====
CREATE TABLE otp_challenges (
    id BINARY(16) NOT NULL,                 -- OTP UUID
    user_id BINARY(16) NOT NULL,             -- FK → users.id
    channel VARCHAR(16) NOT NULL,            -- PHONE | EMAIL
    destination VARCHAR(128) NULL,
    code_hash VARCHAR(128) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    max_attempts INT NOT NULL DEFAULT 5,
    status VARCHAR(16) NOT NULL,             -- PENDING | VERIFIED | EXPIRED | BLOCKED
    txn_id BINARY(16) NOT NULL,               -- UUID instead of VARCHAR
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    verified_at TIMESTAMP(6) NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_otp_txn (txn_id),
    KEY idx_otp_user_channel (user_id, channel),
    CONSTRAINT fk_otp_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;