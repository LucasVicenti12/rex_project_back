/*mysql*/
ALTER TABLE address ADD COLUMN district VARCHAR(60);
UPDATE address SET district = '';
ALTER TABLE address MODIFY COLUMN district VARCHAR(150) NOT NULL;