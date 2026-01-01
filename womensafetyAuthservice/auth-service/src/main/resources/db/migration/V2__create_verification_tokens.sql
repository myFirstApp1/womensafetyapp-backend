CREATE TABLE verification_tokens (
  id BINARY(16) PRIMARY KEY,
  token VARCHAR(255) NOT NULL UNIQUE,
  user_id BINARY(16) NOT NULL,
  expiry_date TIMESTAMP(6) NOT NULL,

  CONSTRAINT fk_verification_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
);