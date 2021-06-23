CREATE TABLE IF NOT EXISTS exchange_transactions (
	id BIGSERIAL NOT NULL,
	version BIGINT,
	
	user_id BIGINT,
	
	base_currency VARCHAR(25),
	base_value DECIMAL,
	rate_currency VARCHAR(25),
	rate_value DECIMAL,
	used_rate DECIMAL,
	
	created_date TIMESTAMP,
	
	PRIMARY KEY(id)
);