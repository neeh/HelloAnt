
/* Inserts a bot into the game server DB.
 * @param pname The name of the bot
 * @return rtoken The token generated for the bot
 */
--DROP FUNCTION IF EXISTS dbants.NewBot;
DELIMITER $$
CREATE FUNCTION dbants.NewBot(pname VARCHAR(16) CHARSET utf8)
	RETURNS CHAR(32) CHARSET utf8
	DETERMINISTIC
	CONTAINS SQL
BEGIN
	DECLARE rtoken CHAR(32) CHARSET utf8 DEFAULT NULL;
	DECLARE n TINYINT;
	SELECT COUNT(*) INTO n FROM bots WHERE name = pname;
	IF n = 0 THEN
		REPEAT
			SET rtoken := MD5(CURRENT_TIMESTAMP + FLOOR(RAND() * 8E3)); -- use CONCAT instead of +
			SELECT COUNT(*) INTO n FROM bots WHERE token = rtoken;
		UNTIL n = 0
		END REPEAT;
		INSERT INTO bots VALUES ('', pname, rtoken, '', NOW(), NULL, NULL);
	END IF;
	RETURN rtoken;
END$$

--Example
DELIMITER ;
SELECT NewBot('myBot56');

--todo: unit tests


/* Inserts a bot into the game server DB.
 * @param[in] pname The name of the bot
 * @param[out] ptoken The token generated for the bot
 * @param[out] perror The procedure error code (0: success, 1: bot name already taken)
 */
--DROP PROCEDURE IF EXISTS dbants.AddBot;
DELIMITER $$
CREATE PROCEDURE dbants.AddBot(IN pname VARCHAR(16) CHARSET utf8, OUT ptoken CHAR(32) CHARSET utf8, OUT perror TINYINT)
	CONTAINS SQL
BEGIN
	DECLARE res TINYINT;
	SET perror := 0;
	SELECT COUNT(*) INTO res FROM bots WHERE name = pname;
	IF res = 0 THEN
		REPEAT
			SET ptoken := MD5(CURRENT_TIMESTAMP + FLOOR(RAND() * 8E3)); -- use CONCAT instead of +
			SELECT COUNT(*) INTO res FROM bots WHERE token = ptoken;
		UNTIL res = 0
		END REPEAT;
		INSERT INTO bots VALUES ('', pname, ptoken, '', NOW(), NULL, NULL);
	ELSE
		SET perror := 1;
	END IF;
END$$

--Example
DELIMITER ;
SET @token := NULL, @error := 0;
CALL AddBot('mybot56', @token, @error);
SELECT @token, @error;


/* Logins a bot on the game server
 * @param ptoken The token generated for the bot
 * @return rname The name of the bot (NULL if login failed)
 */
--DROP FUNCTION IF EXISTS dbants.Login;
DELIMITER $$
CREATE FUNCTION dbants.Login(ptoken CHAR(32) CHARSET utf8, pIP CHAR(15) CHARSET utf8)
	RETURNS VARCHAR(16) CHARSET utf8
	READS SQL DATA
BEGIN
	DECLARE rname VARCHAR(16) CHARSET utf8 DEFAULT NULL;
	SELECT name INTO rname FROM bots WHERE token = ptoken LIMIT 1;
	if rname IS NOT NULL THEN
		UPDATE bots SET lastLoginDate = NOW(), lastIP = pIP WHERE token = ptoken LIMIT 1;
	END IF;
	--todo: should return more than just the name!
	RETURN rname;
END$$

--Example
DELIMITER ;
SELECT Login(tok, '146.23.189.75');


/* Updates the base score value and scales all bot scores.
 * @param pbasescore The new base score value
 * @param pbackup Whether to backup before updating table scheme and bot entries.
 */
--DROP PROCEDURE IF EXISTS dbants.UpdateBaseScore;
DELIMITER $$
CREATE PROCEDURE dbants.UpdateBaseScore(IN pbasescore SMALLINT UNSIGNED, IN pbackup BOOLEAN)
	CONTAINS SQL
BEGIN
	IF pbackup THEN
		COMMIT; --todo
		SAVEPOINT; --??
	END IF;
	DECLARE basescore SMALLINT UNSIGNED;
	DECLARE ratio FLOAT;
	SELECT DEFAULT(score) INTO basescore FROM bots LIMIT 1;
	SET ratio := pbasescore / basescore;
	UPDATE bots SET VALUES--todo ;
	ALTER TABLE bots ALTER COLUMN score SET DEFAULT pbasescore;
END$$

--Example
DELIMITER ;
CALL UpdateBaseScore(2000, TRUE);
