/*postgres*/
ALTER TABLE users ADD COLUMN address VARCHAR(150);
UPDATE users SET address = '' WHERE login <> '';
ALTER TABLE users ALTER COLUMN address SET NOT NULL;

/*mysql*/
ALTER TABLE users ADD COLUMN address VARCHAR(150);
UPDATE users SET address = '' WHERE login <> '';
ALTER TABLE users MODIFY COLUMN address VARCHAR(150) NOT NULL;