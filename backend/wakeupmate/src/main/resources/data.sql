INSERT INTO users (id, username, email, role, status, created_at, modified_at)
VALUES (1, 'test-user', 'test@example.com', 'USER', 'USABLE', NOW(), NOW())
ON DUPLICATE KEY UPDATE username=VALUES(username);


