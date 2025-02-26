DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id VARCHAR(36) PRIMARY KEY,
    manager_id VARCHAR(36),
    full_name VARCHAR(255) NOT NULL,
    mob_num VARCHAR(15) NOT NULL,
    pan_num VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    is_active BOOLEAN NOT NULL
);

DROP TABLE IF EXISTS managers;
CREATE TABLE managers (
    manager_id VARCHAR(36) PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL
);

/* Sample test data for managers:
   - Manager One:
       manager_id: 11111111-1111-1111-1111-111111111111
       full_name: Manager One
       email: manager.one@example.com
   - Manager Two:
       manager_id: 22222222-2222-2222-2222-222222222222
       full_name: Manager Two
       email: manager.two@example.com
*/
INSERT INTO managers (manager_id, full_name, email, is_active, created_at) VALUES
('11111111-1111-1111-1111-111111111111', 'Manager One', 'manager.one@example.com', true, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222222', 'Manager Two', 'manager.two@example.com', true, CURRENT_TIMESTAMP);
