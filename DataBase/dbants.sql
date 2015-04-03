
-- MYSQL DATABASE: dbants
-- 03/04/2015 12:16AM
-- hekat

DELIMITER ;

-- Drop existing database routines
DROP FUNCTION IF EXISTS NewBot;
DROP PROCEDURE IF EXISTS Login;
DROP PROCEDURE IF EXISTS UpdateBotScores;

-- Drop existing tables
DROP TABLE IF EXISTS bots;

-- Scheme for table "bots"
CREATE TABLE bots (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	nick VARCHAR(16) CHARSET utf8 NOT NULL,
	token CHAR(32) CHARSET utf8 NOT NULL,
	score SMALLINT(5) UNSIGNED NOT NULL DEFAULT '1200',
	subscriptionDate DATETIME NOT NULL,
	lastLoginDate DATETIME DEFAULT NULL,
	lastIP CHAR(15) CHARSET utf8 DEFAULT NULL,
	PRIMARY KEY (id),
	UNIQUE KEY nick (nick, token)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8;

-- Dumping data for table "bots"
INSERT INTO bots(id, nick, token, score, subscriptionDate, lastLoginDate, lastIP) VALUES
(1, 'neeh', 'zef4ze6eerg1er21g3f545v4zc1ze313', 1200, '2015-03-25 09:26:37', NULL, NULL),
(2, 'test', 'abc', 1200, '2015-03-25 18:15:28', '2015-03-25 22:31:51', '146.23.189.75'),
(3, 'marco18', '28624b77912520ec68173c4f1b8325dd', 1200, '2015-03-25 18:24:05', NULL, NULL),
(4, 'jerome', 'b29f14bf1ca41f631d2967495848c462', 1200, '2015-03-25 21:13:35', NULL, NULL),
(5, 'truc89', 'fa3698ca8471bfb09b7cbc855d7bf8ca', 1200, '2015-03-25 21:14:13', NULL, NULL),
(6, 'roger1', '772775173343d36278f8e53af8fffb36', 1200, '2015-04-01 12:46:25', NULL, NULL),
(7, 'MyPoLyTeChBoT', '8c4a7a0a3e01d0724643811ff253712d', 1200, '2015-04-01 12:47:44', NULL, NULL);

DELIMITER $$

-- Define database routines

/* Inserts a bot into the game server DB.
 * SELECT NewBot('nick');
 * @param pnick The nickname of the bot
 * @return rtoken The token generated for the bot
 */
CREATE FUNCTION NewBot(pnick VARCHAR(16) CHARSET utf8)
	RETURNS CHAR(32) CHARSET utf8
	DETERMINISTIC
	CONTAINS SQL
BEGIN
	DECLARE rtoken CHAR(32) CHARSET utf8 DEFAULT NULL;
	DECLARE n TINYINT;
	SELECT COUNT(*) INTO n FROM bots WHERE nick = pnick;
	IF n = 0 THEN
		REPEAT
			SET rtoken := MD5(CONCAT(CURRENT_TIMESTAMP, FLOOR(RAND() * 8E3)));
			SELECT COUNT(*) INTO n FROM bots WHERE token = rtoken;
		UNTIL n = 0
		END REPEAT;
		INSERT INTO bots(nick, token, subscriptionDate) VALUES (pnick, rtoken, NOW());
	END IF;
	RETURN rtoken;
END$$

-- todo: unit tests

/* Logins a bot on the game server
 * CALL Login('abc', '146.23.189.75', @onick, @oscore, @oerror);
 * SELECT @onick, @oscore, @oerror;
 * @param[in] itoken The token generated for the bot
 * @param[in] iaddr The IP address of the bot
 * @param[out] onick The nickname of the bot (NULL if login failed)
 * @param[out] oscore The game score of the bot (NULL if login failed)
 * @param[out] oerror The error code of the procedure (0: success, 1: no bot for this token)
 */
CREATE PROCEDURE Login(IN itoken CHAR(32) CHARSET utf8, IN iaddr CHAR(15) CHARSET utf8,
	OUT onick VARCHAR(16) CHARSET utf8, OUT oscore SMALLINT UNSIGNED, OUT oerror TINYINT)
	CONTAINS SQL
BEGIN
	SET oerror := 1;
	SELECT nick, score INTO onick, oscore FROM bots WHERE token = itoken LIMIT 1;
	IF onick IS NOT NULL THEN
		SET oerror := 0;
		-- update bot infos
		UPDATE bots SET lastLoginDate = NOW(), lastIP = iaddr WHERE token = itoken LIMIT 1;
	END IF;
END$$

/* Updates the base score value and scales all bot scores.
 * CALL UpdateBaseScore(2000, TRUE);
 * @param[in] pbasescore The new base score value
 * @param[in] pbackup Whether to backup before updating table scheme and bot entries.
 */
/*CREATE PROCEDURE UpdateBaseScore(IN pbasescore SMALLINT UNSIGNED, IN pbackup BOOLEAN)
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
