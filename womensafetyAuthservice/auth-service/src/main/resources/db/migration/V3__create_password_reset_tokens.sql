CREATE TABLE password_reset_token (
  id BINARY(16) PRIMARY KEY,
  token VARCHAR(255) NOT NULL UNIQUE,
  user_id BINARY(16) NOT NULL,
  expiry_date TIMESTAMP(6) NOT NULL,
  used TINYINT(1) NOT NULL DEFAULT FALSE,

  CONSTRAINT fk_password_reset_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
);