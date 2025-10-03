USE COSMETICS;
DELIMITER //

-- CRUD TABLE USER
-- Create user moi
CREATE PROCEDURE AddUser
(
	IN p_user_id CHAR(36),
	IN p_email VARCHAR(100),
	IN p_fullname VARCHAR(100),
	IN p_password VARCHAR(255),
	IN p_phone_number VARCHAR(20),
	IN p_birthday DATE,
	IN p_gender ENUM('Nam','Nữ')
)
BEGIN
	INSERT INTO user(user_id, email, fullname, password, phone_number, birthday, gender)
    VALUES (p_user_id, p_email, p_fullname, p_password, p_phone_number, p_birthday, p_gender);
END;
//

-- Read: lay toan bo user
CREATE PROCEDURE GetAllUser()
BEGIN
	SELECT * FROM user;
END;
//

-- Read: lay user theo user id
CREATE PROCEDURE GetUserById
(
	IN p_user_id CHAR(36)
)
BEGIN
	SELECT * FROM user WHERE user_id = p_user_id;
END;
//

-- Update: cap nhat User
CREATE PROCEDURE UpdateUser
(
	IN p_user_id CHAR(36),
	IN p_email VARCHAR(100),
	IN p_fullname VARCHAR(100),
	IN p_password VARCHAR(255),
	IN p_phone_number VARCHAR(20),
	IN p_birthday DATE,
	IN p_gender ENUM('Nam','Nữ')
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

-- Delete: xoa user
CREATE PROCEDURE DeleteUser
(
	IN p_user_id CHAR(36)
)
BEGIN
	DELETE FROM user WHERE user_is = p_user_id;
END;
//



-- CRUD TABLE ACCOUNT
-- CREATE: them account
CREATE PROCEDURE AddAccount
(
	IN p_user_id CHAR(36),
	IN p_role_id INT ,
	IN p_is_active BOOLEAN
)
BEGIN
	INSERT INTO account (user_id, role_id, is_active)
    VALUES (p_user_id, p_role_id, p_is_active);
END;
//

-- READ: all account
CREATE PROCEDURE GetAllAccount()
BEGIN
	SELECT * FROM account;
END;
//

-- READ: account by id
CREATE PROCEDURE GetAccountById
(
	IN p_account_id CHAR(36)
)
BEGIN
	SELECT * FROM account WHERE account_id = p_account_id;
END;
//

-- UPDATE: account by id
CREATE PROCEDURE UpdateAccount
(
	IN p_account_id CHAR(36),
	IN p_user_id CHAR(36),
	IN p_role_id INT ,
	IN p_is_active BOOLEAN
)
BEGIN
	UPDATE account
    SET user_id = p_user_id,
        is_active = p_is_active,
        updated_at = CURRENT_TIMESTAMP
	WHERE account_id = p_account_id;
END;
//

-- DELETE: account by id
CREATE PROCEDURE DeleteAccount
(
	IN p_account_id CHAR(36)
)
BEGIN
	UPDATE account
	SET is_active = FALSE,
		updated_at = CURRENT_TIMESTAMP
    WHERE account_id = p_account_id;
END;
//


-- CRUD TABLE customer_type
-- CREATE: them customer_type
CREATE PROCEDURE AddCustomerType (
    IN p_customer_type_name VARCHAR(100)
)
BEGIN
    INSERT INTO customer_type (customer_type_name)
    VALUES (p_customer_type_name);
END;
//

-- READ: all customer_type
CREATE PROCEDURE GetAllCustomerTypes()
BEGIN
    SELECT * FROM customer_type;
END;
//

-- READ: customer_type by id
CREATE PROCEDURE GetCustomerTypeById (
    IN p_customer_type_id INT
)
BEGIN
    SELECT * 
    FROM customer_type
    WHERE customer_type_id = p_customer_type_id;
END;
//

-- UPDATE: customer_type
CREATE PROCEDURE UpdateCustomerType (
    IN p_customer_type_id INT,
    IN p_customer_type_name VARCHAR(100)
)
BEGIN
    UPDATE customer_type
    SET customer_type_name = p_customer_type_name
    WHERE customer_type_id = p_customer_type_id;
END;
//

-- DELETE: customer type
CREATE PROCEDURE DeleteCustomerType (
    IN p_customer_type_id INT
)
BEGIN
    DELETE FROM customer_type WHERE customer_type_id = p_customer_type_id;
END;
//


-- CRUD TABLE CUSTOMER
-- CREATE: them customer
CREATE PROCEDURE AddCustomer
(
	IN p_user_id CHAR(36),
	IN p_customer_type_id INT
)
BEGIN
	INSERT INTO customer(user_id, customer_type_id)
    VALUES (p_user_id, p_customer_type_id);
END;
//

-- READ: all customer
CREATE PROCEDURE GetAllCustomer()
BEGIN
	SELECT  * FROM customer;
END;
//

-- Read: cutomer by id
CREATE PROCEDURE GetCustomerById (
    IN p_customer_id INT
)
BEGIN
    SELECT c.customer_id, c.user_id, c.customer_type_id, ct.customer_type_name, c.updated_at
    FROM customer c
    LEFT JOIN customer_type ct ON c.customer_type_id = ct.customer_type_id
    WHERE c.customer_id = p_customer_id;
END;
//

-- UPDATE: cutomer
CREATE PROCEDURE UpdateCustomer (
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

-- DELETE: customer 
CREATE PROCEDURE DeleteCustomer (
    IN p_customer_id INT
)
BEGIN
    DELETE FROM customer WHERE customer_id = p_customer_id;
END;
//



-- CRUD TABLE manager
-- CREATE: them manager
CREATE PROCEDURE AddManager (
    IN p_user_id CHAR(36)
)
BEGIN
    INSERT INTO manager (user_id)
    VALUES (p_user_id);
END;
//

-- READ: all manager
CREATE PROCEDURE GetAllManagers()
BEGIN
    SELECT m.manager_id, m.user_id, u.fullname, u.email, m.updated_at
    FROM manager m
    JOIN user u ON m.user_id = u.user_id;
END;
//

-- READ: manager by id
CREATE PROCEDURE GetManagerById (
    IN p_manager_id INT
)
BEGIN
    SELECT m.manager_id, m.user_id, u.fullname, u.email, m.updated_at
    FROM manager m
    JOIN user u ON m.user_id = u.user_id
    WHERE m.manager_id = p_manager_id;
END;
//

-- UPDATE: manager
CREATE PROCEDURE UpdateManager (
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


-- CRUD TABLE admin
-- CREATE: them admin
CREATE PROCEDURE AddAdmin (
    IN p_user_id CHAR(36)
)
BEGIN
    INSERT INTO admin (user_id)
    VALUES (p_user_id);
END;
//

-- READ: all admin
CREATE PROCEDURE GetAllAdmins()
BEGIN
    SELECT a.admin_id, a.user_id, u.fullname, u.email, a.updated_at
    FROM admin a
    JOIN user u ON a.user_id = u.user_id;
END;
//

-- READ: admin by id
CREATE PROCEDURE GetAdminById (
    IN p_admin_id INT
)
BEGIN
    SELECT a.admin_id, a.user_id, u.fullname, u.email, a.updated_at
    FROM admin a
    JOIN user u ON a.user_id = u.user_id
    WHERE a.admin_id = p_admin_id;
END;
//

-- UPDATE: admin
CREATE PROCEDURE UpdateAdmin (
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


-- CRUD TABLE address
-- CREATE: them address
CREATE PROCEDURE AddAddress (
    IN p_customer_id INT,
    IN p_city VARCHAR(100),
    IN p_district VARCHAR(100),
    IN p_street VARCHAR(200),
    IN p_phone_number VARCHAR(20),
    IN p_id_address_default BOOLEAN
)
BEGIN
    INSERT INTO address (customer_id, city, district, street, phone_number, id_address_default)
    VALUES (p_customer_id, p_city, p_district, p_street, p_phone_number, p_id_address_default);
END;
//

-- READ: all address
CREATE PROCEDURE GetAllAddresses()
BEGIN
    SELECT a.address_id, a.customer_id, c.user_id, a.city, a.district, a.street, a.phone_number, a.id_address_default, a.created_at
    FROM address a
    JOIN customer c ON a.customer_id = c.customer_id;
END;
//

-- READ: address by id
CREATE PROCEDURE GetAddressById (
    IN p_address_id INT
)
BEGIN
    SELECT a.address_id, a.customer_id, c.user_id, a.city, a.district, a.street, a.phone_number, a.id_address_default, a.created_at
    FROM address a
    JOIN customer c ON a.customer_id = c.customer_id
    WHERE a.address_id = p_address_id;
END;
//

-- UPDATE: address
CREATE PROCEDURE UpdateAddress (
    IN p_address_id INT,
    IN p_city VARCHAR(100),
    IN p_district VARCHAR(100),
    IN p_street VARCHAR(200),
    IN p_phone_number VARCHAR(20),
    IN p_id_address_default BOOLEAN
)
BEGIN
    UPDATE address
    SET city = p_city,
        district = p_district,
        street = p_street,
        phone_number = p_phone_number,
        id_address_default = p_id_address_default
    WHERE address_id = p_address_id;
END;
//

-- DELETE: address
CREATE PROCEDURE DeleteAddress (
    IN p_address_id INT
)
BEGIN
    DELETE FROM address WHERE address_id = p_address_id;
END;
//


-- CRUD TABLE voucher
-- CREATE: them voucher
CREATE PROCEDURE AddVoucher (
    IN p_code VARCHAR(50),
    IN p_description VARCHAR(255),
    IN p_discount_percent INT,
    IN p_max_uses INT,
    IN p_start_date DATETIME,
    IN p_end_date DATETIME
)
BEGIN
    INSERT INTO voucher (code, description, discount_percent, max_uses, start_date, end_date, is_active)
    VALUES (p_code, p_description, p_discount_percent, p_max_uses, p_start_date, p_end_date, TRUE);
END;
//

-- READ: all voucher
CREATE PROCEDURE GetAllVouchers()
BEGIN
    SELECT * FROM voucher;
END;
//

-- READ: voucher by id
CREATE PROCEDURE GetVoucherById (
    IN p_voucher_id INT
)
BEGIN
    SELECT * FROM voucher WHERE voucher_id = p_voucher_id;
END;
//

-- UPDATE: voucher
CREATE PROCEDURE UpdateVoucher (
    IN p_voucher_id INT,
    IN p_description VARCHAR(255),
    IN p_discount_percent INT,
    IN p_max_uses INT,
    IN p_start_date DATETIME,
    IN p_end_date DATETIME,
    IN p_is_active BOOLEAN
)
BEGIN
    UPDATE voucher
    SET description = p_description,
        discount_percent = p_discount_percent,
        max_uses = p_max_uses,
        start_date = p_start_date,
        end_date = p_end_date,
        is_active = p_is_active
    WHERE voucher_id = p_voucher_id;
END;
//

-- DELETE: voucher
CREATE PROCEDURE DeleteVoucher (
    IN p_voucher_id INT
)
BEGIN
    UPDATE voucher
    SET is_active = FALSE
    WHERE voucher_id = p_voucher_id;
END;
//


-- CRUD TABLE categoty
-- CREATE: them category
CREATE PROCEDURE AddCategory (
    IN p_name VARCHAR(150),
    IN p_parent_id INT,
    IN p_slug VARCHAR(150)
)
BEGIN
    INSERT INTO category (name, parent_id, slug)
    VALUES (p_name, p_parent_id, p_slug);
END;
//

-- READ: all category
CREATE PROCEDURE GetAllCategories()
BEGIN
    SELECT c.category_id, c.name, c.parent_id, p.name AS parent_name, c.slug, c.created_at
    FROM category c
    LEFT JOIN category p ON c.parent_id = p.category_id;
END;
//

-- READ: category by id
CREATE PROCEDURE GetCategoryById (
    IN p_category_id INT
)
BEGIN
    SELECT c.category_id, c.name, c.parent_id, p.name AS parent_name, c.slug, c.created_at
    FROM category c
    LEFT JOIN category p ON c.parent_id = p.category_id
    WHERE c.category_id = p_category_id;
END;
//

-- UPDATE: category
CREATE PROCEDURE UpdateCategory (
    IN p_category_id INT,
    IN p_name VARCHAR(150),
    IN p_parent_id INT,
    IN p_slug VARCHAR(150)
)
BEGIN
    UPDATE category
    SET name = p_name,
        parent_id = p_parent_id,
        slug = p_slug
    WHERE category_id = p_category_id;
END;
//

-- DELETE: category
CREATE PROCEDURE DeleteCategory (
    IN p_category_id INT
)
BEGIN
    DELETE FROM category WHERE category_id = p_category_id;
END;
//


-- CRUD: item
-- CREATE: them item
CREATE PROCEDURE AddItem (
    IN p_name VARCHAR(150),
    IN p_description TEXT,
    IN p_color VARCHAR(50),
    IN p_ingredient TEXT,
    IN p_price INT,
    IN p_category_id INT
)
BEGIN
    INSERT INTO item (name, description, color, ingredient, price, category_id, is_active, updating)
    VALUES (p_name, p_description, p_color, p_ingredient, p_price, p_category_id, TRUE, 1);
END;
//

-- READ: all item
CREATE PROCEDURE GetAllItems()
BEGIN
    SELECT i.item_id, i.name, i.description, i.color, i.ingredient,
           i.price, i.category_id, c.name AS category_name,
           i.is_active, i.created_at, i.updated_at, i.updating
    FROM item i
    LEFT JOIN category c ON i.category_id = c.category_id;
END;
//

-- READ: item by id
CREATE PROCEDURE GetItemById (
    IN p_item_id INT
)
BEGIN
    SELECT i.item_id, i.name, i.description, i.color, i.ingredient,
           i.price, i.category_id, c.name AS category_name,
           i.is_active, i.created_at, i.updated_at, i.updating
    FROM item i
    LEFT JOIN category c ON i.category_id = c.category_id
    WHERE i.item_id = p_item_id;
END;
//

-- UPDATE: item
CREATE PROCEDURE UpdateItem (
    IN p_item_id INT,
    IN p_name VARCHAR(150),
    IN p_description TEXT,
    IN p_color VARCHAR(50),
    IN p_ingredient TEXT,
    IN p_price INT,
    IN p_category_id INT,
    IN p_is_active BOOLEAN,
    IN p_updating INT
)
BEGIN
    UPDATE item
    SET name = p_name,
        description = p_description,
        color = p_color,
        ingredient = p_ingredient,
        price = p_price,
        category_id = p_category_id,
        is_active = p_is_active,
        updating = p_updating,
        updated_at = CURRENT_TIMESTAMP
    WHERE item_id = p_item_id;
END;
//

-- DELETE: item
CREATE PROCEDURE DeleteItem (
    IN p_item_id INT
)
BEGIN
    UPDATE item
    SET is_active = FALSE,
        updated_at = CURRENT_TIMESTAMP
    WHERE item_id = p_item_id;
END;
//


-- CRUD TABLE inventory
-- CREATE: them inventory
CREATE PROCEDURE AddInventory (
    IN p_item_id INT,
    IN p_quantity INT,
    IN p_reserved INT
)
BEGIN
    INSERT INTO inventory (item_id, quantity, reserved)
    VALUES (p_item_id, p_quantity, p_reserved);
END;
//

-- READ: all inventory
CREATE PROCEDURE GetAllInventories()
BEGIN
    SELECT inv.item_id, i.name AS item_name,
           inv.quantity, inv.reserved, inv.updated_at
    FROM inventory inv
    LEFT JOIN item i ON inv.item_id = i.item_id;
END;
//

-- READ: inventory by id
CREATE PROCEDURE GetInventoryByItemId (
    IN p_item_id INT
)
BEGIN
    SELECT inv.item_id, i.name AS item_name,
           inv.quantity, inv.reserved, inv.updated_at
    FROM inventory inv
    LEFT JOIN item i ON inv.item_id = i.item_id
    WHERE inv.item_id = p_item_id;
END;
//

-- UPDATE: inventory
CREATE PROCEDURE UpdateInventory (
    IN p_item_id INT,
    IN p_quantity INT,
    IN p_reserved INT
)
BEGIN
    UPDATE inventory
    SET quantity = p_quantity,
        reserved = p_reserved,
        updated_at = CURRENT_TIMESTAMP
    WHERE item_id = p_item_id;
END;
//


DELIMITER ;