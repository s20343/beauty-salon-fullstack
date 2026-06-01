CREATE TABLE salon (
                       id          BIGSERIAL PRIMARY KEY,
                       name        VARCHAR(255) NOT NULL,
                       address     VARCHAR(255),
                       district    VARCHAR(255),
                       phone_number VARCHAR(255),
                       website     VARCHAR(255),
                       services_offered TEXT,
                       price_range VARCHAR(50),
                       rating      FLOAT8,
                       review_count INTEGER,
                       description TEXT,
                       latitude    FLOAT8,
                       longitude   FLOAT8,
                       created_at  TIMESTAMP,
                       updated_at  TIMESTAMP
);