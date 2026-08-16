CREATE TABLE ai_predictions (
    id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,

    request_id VARCHAR(100) NOT NULL,

    prediction VARCHAR(100) NOT NULL,

    confidence DOUBLE,

    risk_score INT,

    risk_level VARCHAR(50),

    danger_level VARCHAR(50),

    recommended_action VARCHAR(255),

    model_version VARCHAR(50),

    created_at DATETIME NOT NULL,

    PRIMARY KEY (id),

    INDEX idx_ai_predictions_user_id (user_id),

    INDEX idx_ai_predictions_request_id (request_id),

    INDEX idx_ai_predictions_created_at (created_at)
);