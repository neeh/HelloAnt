
-- MYSQL DATABASE: dbants
-- 03/04/2015 12:16AM
-- hekat

/* Inserts a bot into the game server DB.
 * @param pname The name of the bot
 * @return rtoken The token generated for the bot
 */
DELIMITER ;
DROP FUNCTION IF EXISTS NewBot;
DELIMITER $$
CREATE FUNCTION NewBot(pname VARCHAR(16) CHARSET utf8)
	RETURNS CHAR(32) CHARSET utf8
	DETERMINISTIC
	CONTAINS SQL
BEGIN
	DECLARE rtoken CHAR(32) CHARSET utf8 DEFAULT NULL;
	DECLARE n TINYINT;
	SELECT COUNT(*) INTO n FROM bots WHERE name = pname;
	IF n = 0 THEN
		REPEAT
			SET rtoken := MD5(CONCAT(CURRENT_TIMESTAMP, FLOOR(RAND() * 8E3)));
			SELECT COUNT(*) INTO n FROM bots WHERE token = rtoken;
		UNTIL n = 0
		END REPEAT;
		INSERT INTO bots(name, token, subscriptionDate) VALUES (pname, rtoken, NOW());
	END IF;
	RETURN rtoken;
END$$

-- SELECT NewBot('myBot56');
-- todo: unit tests

/* Logins a bot on the game server
 * @param ptoken The token generated for the bot
 * @return rname The name of the bot (NULL if login failed)
 */
DELIMITER ;
DROP FUNCTION IF EXISTS Login;
DELIMITER $$
CREATE FUNCTION Login(ptoken CHAR(32) CHARSET utf8, pIP CHAR(15) CHARSET utf8)
	RETURNS VARCHAR(16) CHARSET utf8
	READS SQL DATA
BEGIN
	DECLARE rname VARCHAR(16) CHARSET utf8 DEFAULT NULL;
	SELECT name INTO rname FROM bots WHERE token = ptoken LIMIT 1;
	if rname IS NOT NULL THEN
		UPDATE bots SET lastLoginDate = NOW(), lastIP = pIP WHERE token = ptoken LIMIT 1;
	END IF;
	-- todo: return bot score
	RETURN rname;
END$$

-- SELECT Login(tok, '146.23.189.75');

/* Updates the base score value and scales all bot scores.
 * @param pbasescore The new base score value
 * @param pbackup Whether to backup before updating table scheme and bot entries.
 */
-- DELIMITER ;
-- DROP PROCEDURE IF EXISTS UpdateBaseScore;
/*DELIMITER $$
CREATE PROCEDURE UpdateBaseScore(IN pbasescore SMALLINT UNSIGNED, IN pbackup BOOLEAN)
	CONTAINS SQL
BEGIN
	IF pbackup THEN
		-- COMMIT;
		-- SAVEPOINT;
	END IF;
	DECLARE basescore SMALLINT UNSIGNED;
	DECLARE ratio FLOAT;
	SELECT DEFAULT(score) INTO basescore FROM bots LIMIT 1;
	SET ratio := pbasescore / basescore;
	-- UPDATE bots SET VALUES
	ALTER TABLE bots ALTER COLUMN score SET DEFAULT pbasescore;
END$$*/

-- CALL UpdateBaseScore(2000, TRUE);

DELIMITER ;

-- Table structure for table "bots"
CREATE TABLE IF NOT EXISTS bots (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	name VARCHAR(16) NOT NULL,
	token CHAR(32) NOT NULL,
	score SMALLINT(5) UNSIGNED NOT NULL DEFAULT '1200',
	subscriptionDate DATETIME NOT NULL,
	lastLoginDate DATETIME DEFAULT NULL,
	lastIP CHAR(15) DEFAULT NULL,
	PRIMARY KEY (id),
	UNIQUE KEY name (name, token)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8;

-- Dumping data for table "bots"
INSERT INTO bots(id, name, token, score, subscriptionDate, lastLoginDate, lastIP) VALUES
(1, 'neeh', 'zef4ze6eerg1er21g3f545v4zc1ze313', 1200, '2015-03-25 09:26:37', NULL, NULL),
(2, 'test', 'abc', 1200, '2015-03-25 18:15:28', '2015-03-25 22:31:51', '146.23.189.75'),
(3, 'marco18', '28624b77912520ec68173c4f1b8325dd', 1200, '2015-03-25 18:24:05', NULL, NULL),
(4, 'jerome', 'b29f14bf1ca41f631d2967495848c462', 1200, '2015-03-25 21:13:35', NULL, NULL),
(5, 'truc89', 'fa3698ca8471bfb09b7cbc855d7bf8ca', 1200, '2015-03-25 21:14:13', NULL, NULL),
(6, 'roger1', '772775173343d36278f8e53af8fffb36', 1200, '2015-04-01 12:46:25', NULL, NULL),
(7, 'MyPoLyTeChBoT', '8c4a7a0a3e01d0724643811ff253712d', 1200, '2015-04-01 12:47:44', NULL, NULL);
