-- Tạo Database
DROP DATABASE IF EXISTS COSMETICS;
CREATE DATABASE COSMETICS CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE COSMETICS;

-- Roles (RBAC)
CREATE TABLE roles (
  role_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(255)
) ENGINE=InnoDB;

-- Users 
CREATE TABLE users (
  user_id CHAR(36) PRIMARY KEY,
  email VARCHAR(100) NOT NULL UNIQUE,
  fullname VARCHAR(100),
  password VARCHAR(255) NOT NULL,
  phone_number VARCHAR(20),
  birthday DATE,
  gender ENUM('Nam','Nữ'),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Accounts (đăng nhập)
CREATE TABLE accounts(
  account_id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  role_id INT NOT NULL,
  isActive BOOLEAN NOT NULL DEFAULT TRUE,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_accounts_roles FOREIGN KEY (role_id) REFERENCES roles(role_id)
      ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT fk_accounts_users FOREIGN KEY (user_id) REFERENCES users(user_id)
      ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 
-- Customer types
CREATE TABLE customer_types(
  customer_type_id INT PRIMARY KEY AUTO_INCREMENT,
  customer_type_name VARCHAR(100) -- VIP, quen, vang lai
) ENGINE=InnoDB;

-- Customers (mở rộng cho user có role = customer)
CREATE TABLE customers (
  customer_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id CHAR(36) NOT NULL UNIQUE,
  customer_type_id INT,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_customers_users FOREIGN KEY (user_id) REFERENCES users(user_id)
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_customers_types FOREIGN KEY (customer_type_id) REFERENCES customer_types(customer_type_id)
      ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Managers
CREATE TABLE managers(
  manager_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id CHAR(36) NOT NULL UNIQUE,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_managers_users FOREIGN KEY (user_id) REFERENCES users(user_id)
      ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Admins
CREATE TABLE admins(
  admin_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id CHAR(36) NOT NULL UNIQUE,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_admins_users FOREIGN KEY (user_id) REFERENCES users(user_id)
      ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Addresses (thuộc về customers)
CREATE TABLE addresses (
  address_id INT PRIMARY KEY AUTO_INCREMENT,
  customer_id INT NOT NULL,
  city VARCHAR(100),
  district VARCHAR(100),
  street VARCHAR(200),
  phone_number VARCHAR(20),
  id_address_default BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_addresses_customers FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
      ON DELETE CASCADE
) ENGINE=InnoDB;

-- Vouchers
CREATE TABLE vouchers (
  voucher_id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(255),
  discount_percent INT NOT NULL CHECK (discount_percent BETWEEN 0 AND 100),
  max_uses INT,
  used_count INT DEFAULT 0,
  start_date DATETIME,
  end_date DATETIME,
  isActive BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- Categories
CREATE TABLE categories (
  category_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(150) NOT NULL UNIQUE,
  parent_id INT NULL,
  slug VARCHAR(150) NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(category_id)
      ON DELETE SET NULL
) ENGINE=InnoDB;

-- Items
CREATE TABLE items (
  item_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(150) NOT NULL,
  description TEXT,
  color VARCHAR(50),
  ingredient TEXT,
  price INT NOT NULL CHECK (price >= 0),
  category_id INT,
  isActive BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updating INT NOT NULL DEFAULT 1,
  CONSTRAINT fk_items_categories FOREIGN KEY (category_id) REFERENCES categories(category_id)
      ON DELETE SET NULL
) ENGINE=InnoDB;

-- Item Images
CREATE TABLE item_images (
  item_image_id INT AUTO_INCREMENT PRIMARY KEY,
  item_id INT NOT NULL,
  imageBlob BLOB,
  alt VARCHAR(255),
  is_primary BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_item_images_items FOREIGN KEY (item_id) REFERENCES items(item_id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- Inventory
CREATE TABLE inventory (
  item_id INT PRIMARY KEY AUTO_INCREMENT,
  quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
  reserved INT NOT NULL DEFAULT 0 CHECK (reserved >= 0),
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_inventory_items FOREIGN KEY (item_id) REFERENCES items(item_id)
      ON DELETE CASCADE
) ENGINE=InnoDB;

-- Carts
CREATE TABLE carts (
  cart_id INT PRIMARY KEY AUTO_INCREMENT,
  account_id CHAR(36),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_carts_accounts FOREIGN KEY (account_id) REFERENCES accounts(account_id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE cart_items (
  cart_id INT NOT NULL,
  item_id INT NOT NULL,
  quantity INT NOT NULL CHECK (quantity > 0),
  PRIMARY KEY (cart_id, item_id),
  CONSTRAINT fk_cart_items_carts FOREIGN KEY (cart_id) REFERENCES carts(cart_id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_cart_items_items FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Orders
CREATE TABLE orders (
  order_id INT PRIMARY KEY AUTO_INCREMENT,
  customer_id INT,
  address_id INT,
  voucher_id INT,
  total BIGINT NOT NULL CHECK (total >= 0),
  status ENUM('pending','paid','processing','shipped','completed','cancelled','refunded')
      NOT NULL DEFAULT 'pending',
  placed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_customers FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
  CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES addresses(address_id) ON DELETE SET NULL,
  CONSTRAINT fk_orders_vouchers FOREIGN KEY (voucher_id) REFERENCES vouchers(voucher_id) 
          ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Order Items
CREATE TABLE order_items (
  order_item_id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT NOT NULL,
  item_id INT,
  quantity INT NOT NULL CHECK (quantity > 0),
  pre_discount_price INT NOT NULL CHECK (pre_discount_price >= 0),
  total_price_cents INT NOT NULL CHECK (total_price_cents >= 0),
  CONSTRAINT fk_order_items_orders FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
  CONSTRAINT fk_order_items_items FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Voucher-Customer-Items
CREATE TABLE voucher_customer_items(
  voucher_customer_item_id  INT PRIMARY KEY AUTO_INCREMENT,
  voucher_id INT,
  customer_id INT,
  item_id  INT,
  CONSTRAINT fk_voucher_customer_items_vouchers FOREIGN KEY (voucher_id) REFERENCES vouchers(voucher_id) ON DELETE CASCADE,
  CONSTRAINT fk_voucher_customer_items_customers FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
  CONSTRAINT fk_voucher_customer_items_items FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Payments
CREATE TABLE payments (
  payment_id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT,
  platform VARCHAR(50),
  payment_method ENUM('Tien mat','Chuyen khoan') NOT NULL DEFAULT 'Tien mat',
  status ENUM('initiated','complete','failed','refunded')
     NOT NULL DEFAULT 'initiated',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_payments_orders FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Reviews
CREATE TABLE reviews (
  review_id INT PRIMARY KEY AUTO_INCREMENT,
  item_id INT,
  customer_id INT,
  rating INT CHECK (rating BETWEEN 1 AND 5),
  comment TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reviews_items FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
  CONSTRAINT fk_reviews_customers FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Audit Logs
CREATE TABLE audit_logs (
  audit_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_id CHAR(36),
  action VARCHAR(100) NOT NULL,
  object_id INT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_audit_logs_accounts FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Failed Logins
CREATE TABLE failed_logins (
  failed_login_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_id CHAR(36),
  login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status ENUM('Fail','Success') NOT NULL,
  CONSTRAINT fk_failed_logins_accounts FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Sessions
CREATE TABLE sessions (
  session_id CHAR(36) PRIMARY KEY,
  account_id CHAR(36),
  token VARCHAR(255) NOT NULL,
  end_time DATETIME,
  start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_sessions_accounts FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
) ENGINE=InnoDB;
