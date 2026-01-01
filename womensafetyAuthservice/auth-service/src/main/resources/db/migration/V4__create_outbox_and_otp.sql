-- ==== OUTBOX (transactional publisher for events) ====
CREATE TABLE IF NOT EXISTS outbox_events (
  id BIGINT NOT NULL AUTO_INCREMENT,
  event_id VARCHAR(36) NOT NULL,
  aggregate_type VARCHAR(64) NOT NULL,
  aggregate_id VARCHAR(64) NOT NULL,
  event_type VARCHAR(64) NOT NULL,
  payload JSON NOT NULL,                               -- use LONGTEXT if you're on MySQL < 8
  headers_json JSON NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  published_at TIMESTAMP(6) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_outbox_event_id (event_id),
  KEY idx_outbox_unpublished (published_at),
  KEY idx_outbox_aggregate (aggregate_type, aggregate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==== OTP CHALLENGES (verification) ====
CREATE TABLE IF NOT EXISTS otp_challenges (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  channel VARCHAR(16) NOT NULL,                        -- 'PHONE' | 'EMAIL'
  destination VARCHAR(128) NULL,
  code_hash VARCHAR(128) NOT NULL,
  salt VARCHAR(64) NOT NULL,
  expires_at TIMESTAMP(6) NOT NULL,
  attempts INT NOT NULL DEFAULT 0,
  max_attempts INT NOT NULL DEFAULT 5,
  status VARCHAR(16) NOT NULL,                         -- 'PENDING' | 'VERIFIED' | 'EXPIRED' | 'BLOCKED'
  txn_id VARCHAR(36) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  verified_at TIMESTAMP(6) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY idx_otp_txn (txn_id),
  KEY idx_otp_user_channel (user_id, channel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
