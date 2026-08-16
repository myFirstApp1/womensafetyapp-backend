CREATE TABLE emergency_contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    relation VARCHAR(255),
    CONSTRAINT uk_user_phone UNIQUE (user_id, phone_number),
    CONSTRAINT fk_emergency_user
        FOREIGN KEY (user_id) REFERENCES user_profiles(id)
);