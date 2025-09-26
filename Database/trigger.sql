USE COSMETICS;
DELIMITER //
DROP TRIGGER IF EXISTS before_insert_account;
CREATE TRIGGER before_insert_account
BEFORE INSERT ON account
FOR EACH ROW
BEGIN
    -- Tự động set account_id dựa trên role_id
    IF NEW.account_id IS NULL OR NEW.account_id = '' THEN
        IF NEW.role_id = 1 THEN
            SET NEW.account_id = CONCAT('CU', (SELECT COUNT(*) + 1 FROM accounts WHERE role_id = 1));
        ELSEIF NEW.role_id = 2 THEN
            SET NEW.account_id = CONCAT('MN', (SELECT COUNT(*) + 1 FROM accounts WHERE role_id = 2));
        ELSEIF NEW.role_id = 3 THEN
            SET NEW.account_id = CONCAT('AD', (SELECT COUNT(*) + 1 FROM accounts WHERE role_id = 3));
        END IF;
    END IF;
END;
//

DELIMITER ;
