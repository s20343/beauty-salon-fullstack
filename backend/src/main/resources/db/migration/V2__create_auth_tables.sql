-- Create the users table
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

-- Create the refresh token table
CREATE TABLE refresh_tokens (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               token VARCHAR(255) NOT NULL UNIQUE,
                               expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);