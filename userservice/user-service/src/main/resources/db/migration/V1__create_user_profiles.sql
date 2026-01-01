CREATE TABLE user_profiles (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL UNIQUE,
    name VARCHAR(255),
    phone VARCHAR(255),
    address VARCHAR(255),
    profile_picture_url VARCHAR(2048),
    profile_picture_path VARCHAR(512),
    is_verified BIT(1) DEFAULT 0
);