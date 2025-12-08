USE COSMETICS;

-- CATEGORY STORED PROCEDURES
DROP PROCEDURE IF EXISTS GetRootCategoriesWithChildren;

DELIMITER $$
CREATE PROCEDURE GetRootCategoriesWithChildren()
READS SQL DATA
BEGIN
    SELECT 
        c.category_id,
        c.name,
        c.parent_id,
        c.slug,
        c.created_at,
        child.category_id as child_id,
        child.name as child_name,
        child.slug as child_slug,
        child.parent_id as child_parent_id,
        child.created_at as child_created_at
    FROM category c
    LEFT JOIN category child ON c.category_id = child.parent_id
    WHERE c.parent_id IS NULL
    ORDER BY c.category_id, child.category_id;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS GetCategoryItemCounts;

DELIMITER $$
CREATE PROCEDURE GetCategoryItemCounts()
READS SQL DATA
BEGIN
    SELECT 
        c.name, 
        COUNT(i.item_id) as item_count
    FROM category c 
    LEFT JOIN item i ON c.category_id = i.category_id
    GROUP BY c.category_id, c.name 
    HAVING COUNT(i.item_id) > 0;
END$$
DELIMITER ;

-- ITEM STORED PROCEDURES
DROP PROCEDURE IF EXISTS GetItemByIdWithImages;

DELIMITER $$
CREATE PROCEDURE GetItemByIdWithImages(IN p_item_id INT)
READS SQL DATA
BEGIN
    SELECT 
        i.*,
        img.item_image_id,
        img.image_blob,
        img.alt,
        img.is_primary,
        img.created_at as image_created_at
    FROM item i
    LEFT JOIN item_image img ON i.item_id = img.item_id
    WHERE i.item_id = p_item_id;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS GetItemByIdWithFullData;

DELIMITER $$
CREATE PROCEDURE GetItemByIdWithFullData(IN p_item_id INT)
READS SQL DATA
BEGIN
    SELECT 
        i.item_id,
        i.name,
        i.description,
        i.color,
        i.ingredient,
        i.price,
        i.category_id,
        i.is_active,
        i.created_at,
        i.updated_at,
        i.updating,
        c.category_id as cat_id,
        c.name as category_name,
        c.slug as category_slug,
        c.parent_id as category_parent_id,
        img.item_image_id,
        img.image_blob,
        img.alt,
        img.is_primary,
        img.created_at as img_created_at
    FROM item i
    LEFT JOIN category c ON i.category_id = c.category_id
    LEFT JOIN item_image img ON i.item_id = img.item_id
    WHERE i.item_id = p_item_id;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS GetTop5LatestItems;

DELIMITER $$
CREATE PROCEDURE GetTop5LatestItems()
READS SQL DATA
BEGIN
    SELECT * FROM item 
    ORDER BY created_at DESC 
    LIMIT 5;
END$$
DELIMITER ;

-- ORDER ITEM STORED PROCEDURES
DROP PROCEDURE IF EXISTS GetTop10BestsellersByCategory;

DELIMITER $$
CREATE PROCEDURE GetTop10BestsellersByCategory(IN p_category_id INT)
READS SQL DATA
BEGIN
    SELECT 
        oi.item_id,
        i.name,
        i.price,
        i.description,
        i.color,
        SUM(oi.quantity) as total_sold
    FROM order_item oi
    JOIN item i ON oi.item_id = i.item_id
    WHERE i.category_id = p_category_id
    GROUP BY oi.item_id, i.name, i.price, i.description, i.color
    ORDER BY total_sold DESC
    LIMIT 10;
END$$
DELIMITER ;

-- REVIEW STORED PROCEDURES
DROP PROCEDURE IF EXISTS GetReviewsByItemIdWithUser;

DELIMITER $$
CREATE PROCEDURE GetReviewsByItemIdWithUser(IN p_item_id INT)
READS SQL DATA
BEGIN
    SELECT 
        r.review_id,
        r.item_id,
        r.customer_id,
        r.rating,
        r.comment,
        r.created_at,
        c.customer_id as cust_id,
        c.user_id as cust_user_id,
        u.user_id,
        u.fullname,
        u.email,
        u.phone_number
    FROM review r
    LEFT JOIN customer c ON r.customer_id = c.customer_id
    LEFT JOIN user u ON c.user_id = u.user_id
    WHERE r.item_id = p_item_id
    ORDER BY r.created_at DESC;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS GetAllReviewsWithDetails;

DELIMITER $$
CREATE PROCEDURE GetAllReviewsWithDetails()
READS SQL DATA
BEGIN
    SELECT 
        r.review_id,
        r.item_id,
        r.customer_id,
        r.rating,
        r.comment,
        r.created_at,
        i.item_id as it_id,
        i.name as item_name,
        i.price as item_price,
        c.customer_id as cust_id,
        c.user_id as cust_user_id,
        u.user_id,
        u.fullname,
        u.email
    FROM review r
    LEFT JOIN item i ON r.item_id = i.item_id
    LEFT JOIN customer c ON r.customer_id = c.customer_id
    LEFT JOIN user u ON c.user_id = u.user_id
    ORDER BY r.created_at DESC;
END$$
DELIMITER ;

-- SESSION STORED PROCEDURES
DROP PROCEDURE IF EXISTS GetSessionsByToken;

DELIMITER $$
CREATE PROCEDURE GetSessionsByToken(IN p_token VARCHAR(1000))
READS SQL DATA
BEGIN
    SELECT * FROM session 
    WHERE token = p_token AND end_time IS NULL 
    ORDER BY start_time DESC;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS GetActiveSessionsByAccount;

DELIMITER $$
CREATE PROCEDURE GetActiveSessionsByAccount(IN p_account_id CHAR(36))
READS SQL DATA
BEGIN
    SELECT * FROM session 
    WHERE account_id = p_account_id AND end_time IS NULL;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS CountActiveSessionsByAccount;

DELIMITER $$
CREATE PROCEDURE CountActiveSessionsByAccount(IN p_account_id CHAR(36))
READS SQL DATA
BEGIN
    SELECT COUNT(*) as session_count 
    FROM session 
    WHERE account_id = p_account_id AND end_time IS NULL;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS DeleteExpiredSessions;

DELIMITER $$
CREATE PROCEDURE DeleteExpiredSessions(IN p_current_time DATETIME)
MODIFIES SQL DATA
BEGIN
    DELETE FROM session WHERE end_time < p_current_time;
    SELECT ROW_COUNT() as deleted_count;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS CloseAllSessionsByAccount;

DELIMITER $$
CREATE PROCEDURE CloseAllSessionsByAccount(
    IN p_account_id CHAR(36),
    IN p_end_time DATETIME
)
MODIFIES SQL DATA
BEGIN
    UPDATE session 
    SET end_time = p_end_time 
    WHERE account_id = p_account_id AND end_time IS NULL;
    SELECT ROW_COUNT() as updated_count;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS GetActiveSessionById;

DELIMITER $$
CREATE PROCEDURE GetActiveSessionById(IN p_session_id CHAR(36))
READS SQL DATA
BEGIN
    SELECT * FROM session 
    WHERE session_id = p_session_id AND end_time IS NULL;
END$$
DELIMITER ;

-- VOUCHER CUSTOMER ITEM STORED PROCEDURES
DROP PROCEDURE IF EXISTS GetTop5FlashSaleItems;

DELIMITER $$
CREATE PROCEDURE GetTop5FlashSaleItems()
READS SQL DATA
BEGIN
    SELECT 
        i.item_id,
        i.name,
        i.price,
        i.description,
        i.color,
        MAX(v.discount_percent) as max_discount_percent,
        (i.price * MAX(v.discount_percent) / 100) as discount_amount,
        (i.price - (i.price * MAX(v.discount_percent) / 100)) as final_price
    FROM voucher_customer_item vci
    JOIN item i ON vci.item_id = i.item_id
    JOIN voucher v ON vci.voucher_id = v.voucher_id
    WHERE v.is_active = true
      AND v.start_date <= NOW()
      AND v.end_date >= NOW()
    GROUP BY i.item_id, i.name, i.price, i.description, i.color
    ORDER BY discount_amount DESC
    LIMIT 5;
END$$
DELIMITER ;