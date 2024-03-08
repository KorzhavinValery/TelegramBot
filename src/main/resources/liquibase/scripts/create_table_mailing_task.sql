CREATE TABLE IF NOT EXISTS mailing_task

(
id BIGINT PRIMARY KEY generated always as identity,
chat_id BIGINT NOT NULL,
message varchar(255) NOT NULL,
date_time TIMESTAMP

);