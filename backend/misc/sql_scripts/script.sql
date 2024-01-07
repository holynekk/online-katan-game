DROP TABLE IF EXISTS password_reset_token;
DROP TABLE IF EXISTS game_history;
DROP TABLE IF EXISTS game_info;
DROP TABLE IF EXISTS katan_user;

CREATE TABLE katan_user (
	user_id INT AUTO_INCREMENT NOT NULL,
	username VARCHAR(150) NOT NULL UNIQUE,
	first_name VARCHAR(64),
	last_name VARCHAR(64),
	email VARCHAR(320) NOT NULL UNIQUE,
	password_hash VARCHAR(100) NOT NULL,
	salt VARCHAR(16) NOT NULL UNIQUE,
	display_name VARCHAR(50),
	last_password_update DATETIME,
	PRIMARY KEY (user_id)
);
INSERT INTO
  katan_user (user_id, username, first_name, last_name, email, password_hash, salt, display_name, last_password_update) 
VALUES
  (
    1,
    '278EE5F5F523A7CAF68CD1A222C667DC',
    'Test',
    'ADMIN',
    'test_admin@gmail.com',
    '$2y$05$RjjMZBLsL0e1bFTVMVG3SOp4U/mf7qRFaq5GqcwzHVDkuk4NwPJyG',
    'wnNJiOOYM3L9OdFq',
    'Admin',
    '2023-10-07 21:25:56'
  ),
  (
    2,
    'bbcf47d0bd3f8325f42dd7f4f857fa1f',
    'Test',
    'ADMIN2',
    'test_admin2@gmail.com',
    '$2y$05$ajTFUDTNTUDLZSrfajXFKeftvRpKUkBXNNaSrZPM9SLK1azgnIzOO',
    'rUGXUOUaMmKarVG2',
    'Admin2',
    '2023-10-09 21:25:56'
  ),
  (
    3,
    'a52e8b89c5895cef6a452a8488abc002',
    'Test',
    'ADMIN3',
    'test_admin3@gmail.com',
    '$2y$05$Yyu3PRHWLjbWbyTnchH4P.g9QdqsrWlaJ5.2PV4O0mrhmesyi5U4.',
    'kL9E2X6WXwEiz2zD',
    'Admin3',
    '2023-10-07 21:25:56'
  )
;

CREATE TABLE password_reset_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expiry_date DATETIME NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES katan_user(user_id) ON DELETE CASCADE
);


CREATE TABLE game_info (
	game_id INT AUTO_INCREMENT NOT NULL,
	game_name VARCHAR(32) NOT NULL,
	game_description VARCHAR(64),
	game_password VARCHAR(32),
	password_required BOOL,
	game_leader INT NOT NULL,
	is_online BOOL,
	is_started BOOL,
	is_finished BOOL,
	PRIMARY KEY (game_id),
	FOREIGN KEY (game_leader) REFERENCES katan_user(user_id) ON DELETE CASCADE
);

INSERT INTO
  game_info (game_id, game_name, game_description, game_password, password_required, game_leader, is_online, is_started, is_finished)
VALUES
  (
    1, "lobby one", "example game object 1", null, false, 2, true, false, true
  ),
  (
    2, "lobby two", "example game object 2", "katanEX34pass", true, 2, true, false, true
  ),
  (
    3, "lobby three", "example game object 3", null, false, 2, true, false, true
  ),
  (
    4, "lobby four", "example game object 4", null, false, 2, true, false, true
  ),
  (
    5, "lobby five", "example game object 5", null, false, 2, true, true, false
  ),
  (
    6, "lobby six", "example game object 6", null, false, 3, true, true, false
  )
;

CREATE TABLE game_history (
	id INT AUTO_INCREMENT NOT NULL,
	total_score INT,
	did_won BOOL,
	history DATETIME,
	user_id INT NOT NULL,
	game_id INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES katan_user(user_id) ON DELETE CASCADE,
	FOREIGN KEY (game_id) REFERENCES game_info(game_id) ON DELETE CASCADE
);
INSERT INTO
  game_history (id, total_score, did_won, history, user_id, game_id) 
VALUES
  (1, 10, true, '2023-11-06 21:25:56', 1, 1),
  (2, 15, false, '2023-11-07 21:25:56', 1, 2),
  (3, 10, false, '2023-11-01 21:25:56', 1, 3),
  (4, 10, false, '2023-11-01 21:22:56', 1, 4),
  (5, 20, false, '2023-11-08 21:25:56', 2, 1),
  (6, 10, true, '2023-11-01 21:25:56', 2, 2),
  (7, 50, false, '2023-09-07 21:25:56', 2, 3),
  (8, 10, false, '2023-09-07 21:25:56', 2, 4),
  (9, 20, false, '2023-11-08 21:25:56', 3, 1),
  (10, 40, false, '2023-11-01 21:25:56', 3, 2),
  (11, 10, true, '2023-09-07 21:25:56', 3, 3),
  (12, 50, true, '2023-09-07 21:25:56', 3, 4)
;

