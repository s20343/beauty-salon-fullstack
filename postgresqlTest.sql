-- list all tables
\dt

-- see all salons
SELECT id, name, district, rating FROM salon LIMIT 10 OFFSET 2;

-- count by district
SELECT district, COUNT(*) FROM salon GROUP BY district ORDER BY COUNT(*) DESC;

-- check users table
SELECT id, username, role FROM users;

-- check refresh tokens
SELECT id, token, expires_at FROM refresh_tokens;

-- check a specific salon
SELECT * FROM salon WHERE id = 42;

-- test your filter query
SELECT name, district FROM salon
WHERE LOWER(district) = LOWER('Mokotów')
  AND LOWER(services_offered) LIKE LOWER('%massage%');

-- exit psql
\q