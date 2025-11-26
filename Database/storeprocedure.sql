

-- ==========================================================
-- USER TABLE
-- ==========================================================

CREATE PROCEDURE AddUser (
    IN p_user_id CHAR(36),
    IN p_email VARCHAR(100),
    IN p_fullname VARCHAR(100),
    IN p_password VARCHAR(255),
    IN p_phone_number VARCHAR(20),
    IN p_birthday DATE,
    IN p_gender VARCHAR(10)
)
BEGIN
    INSERT INTO user(user_id, email, fullname, password, phone_number, birthday, gender)
    VALUES (p_user_id, p_email, p_fullname, p_password, p_phone_number, p_birthday, p_gender);
END;
//

CREATE PROCEDURE GetAllUser()
BEGIN
    SELECT * FROM user;
END;
//

CREATE PROCEDURE GetUserById(IN p_user_id CHAR(36))
BEGIN
    SELECT * FROM user WHERE user_id = p_user_id;
END;
//

CREATE PROCEDURE UpdateUser (
    IN p_user_id CHAR(36),
    IN p_email VARCHAR(100),
    IN p_fullname VARCHAR(100),
    IN p_password VARCHAR(255),
    IN p_phone_number VARCHAR(20),
    IN p_birthday DATE,
    IN p_gender VARCHAR(10)
)
BEGIN
    UPDATE user
    SET email = p_email,
        fullname = p_fullname,
        password = p_password,
        phone_number = p_phone_number,
        birthday = p_birthday,
        gender = p_gender
    WHERE user_id = p_user_id;
END;
//

CREATE PROCEDURE DeleteUser (IN p_user_id CHAR(36))
BEGIN
    DELETE FROM user WHERE user_id = p_user_id;
END;
//


-- ==========================================================
-- ACCOUNT TABLE
-- ==========================================================

CREATE PROCEDURE AddAccount (
    IN p_user_id CHAR(36),
    IN p_role_id INT,
    IN p_is_active BOOLEAN
)
BEGIN
    INSERT INTO account (user_id, role_id, is_active)
    VALUES (p_user_id, p_role_id, p_is_active);
END;
//

CREATE PROCEDURE GetAllAccount()
BEGIN
    SELECT * FROM account;
END;
//

CREATE PROCEDURE GetAccountById(IN p_account_id CHAR(36))
BEGIN
    SELECT * FROM account WHERE account_id = p_account_id;
END;
//

CREATE PROCEDURE UpdateAccount (
    IN p_account_id CHAR(36),
    IN p_user_id CHAR(36),
    IN p_role_id INT,
    IN p_is_active BOOLEAN
)
BEGIN
    UPDATE account
    SET user_id = p_user_id,
        role_id = p_role_id,
        is_active = p_is_active,
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = p_account_id;
END;
//

CREATE PROCEDURE DeleteAccount(IN p_account_id CHAR(36))
BEGIN
    UPDATE account
    SET is_active = FALSE,
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = p_account_id;
END;
//


-- ==========================================================
-- CUSTOMER TYPE TABLE
-- ==========================================================

CREATE PROCEDURE AddCustomerType(IN p_customer_type_name VARCHAR(100))
BEGIN
    INSERT INTO customer_type (customer_type_name) VALUES (p_customer_type_name);
END;
//

CREATE PROCEDURE GetAllCustomerTypes()
BEGIN
    SELECT * FROM customer_type;
END;
//

CREATE PROCEDURE GetCustomerTypeById(IN p_customer_type_id INT)
BEGIN
    SELECT * FROM customer_type WHERE customer_type_id = p_customer_type_id;
END;
//

CREATE PROCEDURE UpdateCustomerType(
    IN p_customer_type_id INT,
    IN p_customer_type_name VARCHAR(100)
)
BEGIN
    UPDATE customer_type
    SET customer_type_name = p_customer_type_name
    WHERE customer_type_id = p_customer_type_id;
END;
//

CREATE PROCEDURE DeleteCustomerType(IN p_customer_type_id INT)
BEGIN
    DELETE FROM customer_type WHERE customer_type_id = p_customer_type_id;
END;
//


-- ==========================================================
-- CUSTOMER TABLE
-- ==========================================================

CREATE PROCEDURE AddCustomer(IN p_user_id CHAR(36), IN p_customer_type_id INT)
BEGIN
    INSERT INTO customer(user_id, customer_type_id)
    VALUES (p_user_id, p_customer_type_id);
END;
//

CREATE PROCEDURE GetAllCustomer()
BEGIN
    SELECT * FROM customer;
END;
//

CREATE PROCEDURE GetCustomerById(IN p_customer_id INT)
BEGIN
    SELECT c.customer_id, c.user_id, c.customer_type_id, ct.customer_type_name, c.updated_at
    FROM customer c
    LEFT JOIN customer_type ct ON c.customer_type_id = ct.customer_type_id
    WHERE c.customer_id = p_customer_id;
END;
//

CREATE PROCEDURE UpdateCustomer(
    IN p_customer_id INT,
    IN p_customer_type_id INT
)
BEGIN
    UPDATE customer
    SET customer_type_id = p_customer_type_id,
        updated_at = CURRENT_TIMESTAMP
    WHERE customer_id = p_customer_id;
END;
//

CREATE PROCEDURE DeleteCustomer(IN p_customer_id INT)
BEGIN
    DELETE FROM customer WHERE customer_id = p_customer_id;
END;
//


-- ==========================================================
-- MANAGER TABLE
-- ==========================================================

CREATE PROCEDURE AddManager(IN p_user_id CHAR(36))
BEGIN
    INSERT INTO manager(user_id)
    VALUES (p_user_id);
END;
//

CREATE PROCEDURE GetAllManager()
BEGIN
    SELECT * FROM manager;
END;
//

CREATE PROCEDURE GetManagerById(IN p_manager_id INT)
BEGIN
    SELECT * FROM manager WHERE manager_id = p_manager_id;
END;
//

CREATE PROCEDURE UpdateManager(
    IN p_manager_id INT,
    IN p_user_id CHAR(36)
)
BEGIN
    UPDATE manager
    SET user_id = p_user_id,
        updated_at = CURRENT_TIMESTAMP
    WHERE manager_id = p_manager_id;
END;
//

CREATE PROCEDURE DeleteManager(IN p_manager_id INT)
BEGIN
    DELETE FROM manager WHERE manager_id = p_manager_id;
END;
//


-- ==========================================================
-- ADMIN TABLE
-- ==========================================================

CREATE PROCEDURE AddAdmin(IN p_user_id CHAR(36))
BEGIN
    INSERT INTO admin(user_id)
    VALUES (p_user_id);
END;
//

CREATE PROCEDURE GetAllAdmin()
BEGIN
    SELECT * FROM admin;
END;
//

CREATE PROCEDURE GetAdminById(IN p_admin_id INT)
BEGIN
    SELECT * FROM admin WHERE admin_id = p_admin_id;
END;
//

CREATE PROCEDURE UpdateAdmin(
    IN p_admin_id INT,
    IN p_user_id CHAR(36)
)
BEGIN
    UPDATE admin
    SET user_id = p_user_id,
        updated_at = CURRENT_TIMESTAMP
    WHERE admin_id = p_admin_id;
END;
//

CREATE PROCEDURE DeleteAdmin(IN p_admin_id INT)
BEGIN
    DELETE FROM admin WHERE admin_id = p_admin_id;
END;
//


-- ==========================================================
-- ADDRESS TABLE
-- ==========================================================

CREATE PROCEDURE AddAddress(
    IN p_customer_id INT,
    IN p_address_detail VARCHAR(255),
    IN p_city VARCHAR(100),
    IN p_district VARCHAR(100),
    IN p_ward VARCHAR(100)
)
BEGIN
    INSERT INTO address(customer_id, address_detail, city, district, ward)
    VALUES (p_customer_id, p_address_detail, p_city, p_district, p_ward);
END;
//

CREATE PROCEDURE GetAllAddress()
BEGIN
    SELECT * FROM address;
END;
//

CREATE PROCEDURE GetAddressById(IN p_address_id INT)
BEGIN
    SELECT * FROM address WHERE address_id = p_address_id;
END;
//

CREATE PROCEDURE UpdateAddress(
    IN p_address_id INT,
    IN p_address_detail VARCHAR(255),
    IN p_city VARCHAR(100),
    IN p_district VARCHAR(100),
    IN p_ward VARCHAR(100)
)
BEGIN
    UPDATE address
    SET address_detail = p_address_detail,
        city = p_city,
        district = p_district,
        ward = p_ward,
        updated_at = CURRENT_TIMESTAMP
    WHERE address_id = p_address_id;
END;
//

CREATE PROCEDURE DeleteAddress(IN p_address_id INT)
BEGIN
    DELETE FROM address WHERE address_id = p_address_id;
END;
//


-- ==========================================================
-- VOUCHER TABLE
-- ==========================================================

CREATE PROCEDURE AddVoucher(
    IN p_code VARCHAR(50),
    IN p_discount_percent INT,
    IN p_expired_at DATETIME
)
BEGIN
    INSERT INTO voucher(code, discount_percent, expired_at)
    VALUES (p_code, p_discount_percent, p_expired_at);
END;
//

CREATE PROCEDURE GetAllVoucher()
BEGIN
    SELECT * FROM voucher;
END;
//

CREATE PROCEDURE GetVoucherById(IN p_voucher_id INT)
BEGIN
    SELECT * FROM voucher WHERE voucher_id = p_voucher_id;
END;
//

CREATE PROCEDURE UpdateVoucher(
    IN p_voucher_id INT,
    IN p_code VARCHAR(50),
    IN p_discount_percent INT,
    IN p_expired_at DATETIME
)
BEGIN
    UPDATE voucher
    SET code = p_code,
        discount_percent = p_discount_percent,
        expired_at = p_expired_at,
        updated_at = CURRENT_TIMESTAMP
    WHERE voucher_id = p_voucher_id;
END;
//

CREATE PROCEDURE DeleteVoucher(IN p_voucher_id INT)
BEGIN
    DELETE FROM voucher WHERE voucher_id = p_voucher_id;
END;
//


-- ==========================================================
-- CATEGORY TABLE
-- ==========================================================

CREATE PROCEDURE AddCategory(IN p_category_name VARCHAR(100))
BEGIN
    INSERT INTO category(category_name)
    VALUES (p_category_name);
END;
//

CREATE PROCEDURE GetAllCategory()
BEGIN
    SELECT * FROM category;
END;
//

CREATE PROCEDURE GetCategoryById(IN p_category_id INT)
BEGIN
    SELECT * FROM category WHERE category_id = p_category_id;
END;
//

CREATE PROCEDURE UpdateCategory(
    IN p_category_id INT,
    IN p_category_name VARCHAR(100)
)
BEGIN
    UPDATE category
    SET category_name = p_category_name,
        updated_at = CURRENT_TIMESTAMP
    WHERE category_id = p_category_id;
END;
//

CREATE PROCEDURE DeleteCategory(IN p_category_id INT)
BEGIN
    DELETE FROM category WHERE category_id = p_category_id;
END;
//


-- ==========================================================
-- ITEM TABLE
-- ==========================================================

CREATE PROCEDURE AddItem(
    IN p_category_id INT,
    IN p_item_name VARCHAR(100),
    IN p_price DECIMAL(10,2),
    IN p_description TEXT
)
BEGIN
    INSERT INTO item(category_id, item_name, price, description)
    VALUES (p_category_id, p_item_name, p_price, p_description);
END;
//

CREATE PROCEDURE GetAllItem()
BEGIN
    SELECT * FROM item;
END;
//

CREATE PROCEDURE GetItemById(IN p_item_id INT)
BEGIN
    SELECT * FROM item WHERE item_id = p_item_id;
END;
//

CREATE PROCEDURE UpdateItem(
    IN p_item_id INT,
    IN p_category_id INT,
    IN p_item_name VARCHAR(100),
    IN p_price DECIMAL(10,2),
    IN p_description TEXT
)
BEGIN
    UPDATE item
    SET category_id = p_category_id,
        item_name = p_item_name,
        price = p_price,
        description = p_description,
        updated_at = CURRENT_TIMESTAMP
    WHERE item_id = p_item_id;
END;
//

CREATE PROCEDURE DeleteItem(IN p_item_id INT)
BEGIN
    DELETE FROM item WHERE item_id = p_item_id;
END;
//


-- ==========================================================
-- INVENTORY TABLE
-- ==========================================================

CREATE PROCEDURE AddInventory(
    IN p_item_id INT,
    IN p_quantity INT
)
BEGIN
    INSERT INTO inventory(item_id, quantity)
    VALUES (p_item_id, p_quantity);
END;
//

CREATE PROCEDURE GetAllInventory()
BEGIN
    SELECT * FROM inventory;
END;
//

CREATE PROCEDURE GetInventoryById(IN p_inventory_id INT)
BEGIN
    SELECT * FROM inventory WHERE inventory_id = p_inventory_id;
END;
//

CREATE PROCEDURE UpdateInventory(
    IN p_inventory_id INT,
    IN p_item_id INT,
    IN p_quantity INT
)
BEGIN
    UPDATE inventory
    SET item_id = p_item_id,
        quantity = p_quantity,
        updated_at = CURRENT_TIMESTAMP
    WHERE inventory_id = p_inventory_id;
END;
//

CREATE PROCEDURE DeleteInventory(IN p_inventory_id INT)
BEGIN
    DELETE FROM inventory WHERE inventory_id = p_inventory_id;
END;
//

DELIMITER ;
