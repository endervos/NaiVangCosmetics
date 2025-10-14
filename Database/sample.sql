USE COSMETICS;

INSERT INTO role (name, description) VALUES
('Customer','Khách hàng'),
('Manager','Quản lý'),
('Admin','Quản trị viên');

INSERT INTO user (user_id, email, fullname, password, phone_number, birthday, gender) VALUES
('ADMIN001','n22dcat057@student.ptithcm.edu.vn','Trần Phúc Tiến','$2a$12$Z1DeM9N3Tpaxl.MFI6aTRuOPTVwQlxPBcvb0fzOTSfxOhTe1GWNKi','0912345671','2004-01-01','Nam'),
('ADMIN002','n22dcat021@student.ptithcm.edu.vn','Lại Thị Thanh Hiền','$2a$12$rf45r6J3rrfiS0RbvzCUBePAy2ygBhiQIp6KSC5z5eLyybmDbVP/S','0912345672','2004-01-01','Nữ'),
('ADMIN003','n22dcat027@student.ptithcm.edu.vn','Đỗ Tấn Hưng','$2a$12$Pa.GaG8x0iIhSU6NOjmP8e5sGI2A7Tc/NJTKIn91bg0s4iDQt//r2','0912345673','2004-01-01','Nam'),
('ADMIN004','n22dcat040@student.ptithcm.edu.vn','Trần Thị Ánh Nguyệt','$2a$12$5UHATorES75i5pKwnB4/4uktBISD8s.tt8nuGqhbegaEVhbFHKhV.','0912345674','2004-01-01','Nữ'),
('ADMIN005','n22dcat055@student.ptithcm.edu.vn','Nguyễn Thị Lam Thuyên','$2a$12$68mqj/6iNuCI7hEP2KMm3ehjciQcuIIGselz6YG0tPr2PQHzQxV02','0912345675','2004-01-01','Nữ'),
('MANAGER001','manager001@gmail.com','Hoang Van A','$2a$12$ZDQfHwK49S1Hy.pE5El.j.KTGNDoeM0xlGZ05FT2EkUi8h8mLpc2m','0912345676','2000-01-01','Nam'),
('CUSTOMER001','customer001@gmail.com','Nguyen Thi B','$2a$12$ROGt9ALsd1oTUZIsSSI9Wun1e.fxzrTl3r/nkd7/tq6Mv22CnhQt2','0912345677','2001-10-02','Nữ'),
('CUSTOMER002','customer002@gmail.com','Tran Van C','$2a$12$w8nVbSSvZet/7SJRJgJmGeC3WTWT115THkI2ycXxquXI..ijOddhW','0912345678','2000-07-07','Nam'),
('CUSTOMER003','customer003@gmail.com','Le Thi D','$2a$12$HaEmsd/qr0XqKlUl3f1KZO1iPF8zKpnpUa0lXsOD/07HZIV6piH9K','0912345679','2002-08-08','Nữ'),
('CUSTOMER004','customer004@gmail.com','Pham Van E','$2a$12$aStM5QsHM7a/ajcPNGva0OE/7mAMS4jg/2.oVNCKjotTAl/YRUtDy','0912345680','2001-09-09','Nam');

INSERT INTO account (account_id, user_id, role_id, is_active) VALUES
('ACC001','ADMIN001',3,1),
('ACC002','ADMIN002',3,1),
('ACC003','ADMIN003',3,1),
('ACC004','ADMIN004',3,1),
('ACC005','ADMIN005',3,1),
('ACC006','MANAGER001',2,1),
('ACC007','CUSTOMER001',1,1),
('ACC008','CUSTOMER002',1,1),
('ACC009','CUSTOMER003',1,1),
('ACC010','CUSTOMER004',1,1);

INSERT INTO customer_type (customer_type_name) VALUES
('Khách VIP'),
('Khách quen'),
('Khách vãng lai');

INSERT INTO customer (user_id, customer_type_id) VALUES
('CUSTOMER001',1),
('CUSTOMER002',2),
('CUSTOMER003',3),
('CUSTOMER004',1);

INSERT INTO manager (user_id) VALUES
('MANAGER001');

INSERT INTO admin (user_id) VALUES
('ADMIN001'),
('ADMIN002'),
('ADMIN003'),
('ADMIN004'),
('ADMIN005');

INSERT INTO address (customer_id, city, district, street, phone_number, id_address_default) VALUES
(1,'Ha Noi','Dong Da','Duong A','0912345677',1),
(2,'Ha Noi','Cau Giay','Duong B','0912345678',0),
(3,'Hai Phong','Le Chan','Duong C','0912345679',1),
(4,'Da Nang','Hai Chau','Duong D','0912345680',0);

INSERT INTO voucher (code, description, discount_percent, max_uses, start_date, end_date) VALUES
('VOUCHER1','Giam 10%',10,100,'2025-09-01','2025-12-31'),
('VOUCHER2','Giam 15%',15,50,'2025-09-01','2025-12-31'),
('VOUCHER3','Giam 20%',20,30,'2025-09-01','2025-12-31'),
('VOUCHER4','Giam 5%',5,200,'2025-09-01','2025-12-31'),
('VOUCHER5','Giam 25%',25,10,'2025-09-01','2025-12-31'),
('VOUCHER6','Giam 10%',10,100,'2025-09-01','2025-12-31'),
('VOUCHER7','Giam 15%',15,50,'2025-09-01','2025-12-31'),
('VOUCHER8','Giam 20%',20,30,'2025-09-01','2025-12-31'),
('VOUCHER9','Giam 5%',5,200,'2025-09-01','2025-12-31'),
('VOUCHER10','Giam 25%',25,10,'2025-09-01','2025-12-31');

INSERT INTO category (name, slug) VALUES
('Skincare','skincare'),
('Makeup','makeup'),
('Perfume','perfume'),
('Haircare','haircare'),
('Body','body'),
('Men','men'),
('Women','women'),
('Kids','kids'),
('Accessories','accessories'),
('Others','others');

-- Category con Skincare
INSERT INTO category (name, parent_id, slug)
SELECT 'Cleansers', category_id, 'cleansers' FROM category WHERE slug='skincare';
INSERT INTO category (name, parent_id, slug)
SELECT 'Moisturizers', category_id, 'moisturizers' FROM category WHERE slug='skincare';
INSERT INTO category (name, parent_id, slug)
SELECT 'Serums', category_id, 'serums' FROM category WHERE slug='skincare';

-- Category con Makeup
INSERT INTO category (name, parent_id, slug)
SELECT 'Lipsticks', category_id, 'lipsticks' FROM category WHERE slug='makeup';
INSERT INTO category (name, parent_id, slug)
SELECT 'Foundations', category_id, 'foundations' FROM category WHERE slug='makeup';
INSERT INTO category (name, parent_id, slug)
SELECT 'Mascaras', category_id, 'mascaras' FROM category WHERE slug='makeup';

-- Category con Perfume
INSERT INTO category (name, parent_id, slug)
SELECT 'Eau de Parfum', category_id, 'eau-de-parfum' FROM category WHERE slug='perfume';
INSERT INTO category (name, parent_id, slug)
SELECT 'Eau de Toilette', category_id, 'eau-de-toilette' FROM category WHERE slug='perfume';

-- Category con Haircare
INSERT INTO category (name, parent_id, slug)
SELECT 'Shampoo', category_id, 'shampoo' FROM category WHERE slug='haircare';
INSERT INTO category (name, parent_id, slug)
SELECT 'Conditioner', category_id, 'conditioner' FROM category WHERE slug='haircare';
INSERT INTO category (name, parent_id, slug)
SELECT 'Hair Oil', category_id, 'hair-oil' FROM category WHERE slug='haircare';

-- Category con Body
INSERT INTO category (name, parent_id, slug)
SELECT 'Body Lotion', category_id, 'body-lotion' FROM category WHERE slug='body';
INSERT INTO category (name, parent_id, slug)
SELECT 'Body Wash', category_id, 'body-wash' FROM category WHERE slug='body';

-- Category con Men
INSERT INTO category (name, parent_id, slug)
SELECT 'Men Skincare', category_id, 'men-skincare' FROM category WHERE slug='men';
INSERT INTO category (name, parent_id, slug)
SELECT 'Men Perfume', category_id, 'men-perfume' FROM category WHERE slug='men';

-- Category con Women
INSERT INTO category (name, parent_id, slug)
SELECT 'Women Skincare', category_id, 'women-skincare' FROM category WHERE slug='women';
INSERT INTO category (name, parent_id, slug)
SELECT 'Women Perfume', category_id, 'women-perfume' FROM category WHERE slug='women';

-- Category con Kids
INSERT INTO category (name, parent_id, slug)
SELECT 'Kids Shampoo', category_id, 'kids-shampoo' FROM category WHERE slug='kids';
INSERT INTO category (name, parent_id, slug)
SELECT 'Kids Lotion', category_id, 'kids-lotion' FROM category WHERE slug='kids';

-- Category con Accessories
INSERT INTO category (name, parent_id, slug)
SELECT 'Makeup Brushes', category_id, 'makeup-brushes' FROM category WHERE slug='accessories';
INSERT INTO category (name, parent_id, slug)
SELECT 'Hair Accessories', category_id, 'hair-accessories' FROM category WHERE slug='accessories';

INSERT INTO item (name, description, color, ingredient, price, category_id) VALUES
('Item1','Mo ta 1','Red','Ingredient1',100000,1),
('Item2','Mo ta 2','Blue','Ingredient2',150000,2),
('Item3','Mo ta 3','Green','Ingredient3',200000,3),
('Item4','Mo ta 4','Yellow','Ingredient4',250000,4),
('Item5','Mo ta 5','Black','Ingredient5',300000,5),
('Item6','Mo ta 6','White','Ingredient6',120000,6),
('Item7','Mo ta 7','Pink','Ingredient7',180000,7),
('Item8','Mo ta 8','Purple','Ingredient8',220000,8),
('Item9','Mo ta 9','Orange','Ingredient9',270000,9),
('Item10','Mo ta 10','Grey','Ingredient10',320000,10);

INSERT INTO item_image (item_id, alt, is_primary) VALUES
(1,'Image1',1),
(2,'Image2',1),
(3,'Image3',1),
(4,'Image4',1),
(5,'Image5',1),
(6,'Image6',1),
(7,'Image7',1),
(8,'Image8',1),
(9,'Image9',1),
(10,'Image10',1);

INSERT INTO inventory (item_id, quantity, reserved) VALUES
(1,100,0),
(2,200,0),
(3,150,0),
(4,120,0),
(5,80,0),
(6,300,0),
(7,250,0),
(8,180,0),
(9,90,0),
(10,50,0);

INSERT INTO cart (account_id) VALUES
('ACC007'),
('ACC008'),
('ACC009'),
('ACC010'),
('ACC006'),
('ACC001'),
('ACC002');

INSERT INTO cart_item (cart_id, item_id, quantity) VALUES
(1,1,2),
(2,2,1),
(3,3,4),
(4,4,2),
(5,5,3),
(6,6,1),
(7,7,2);

INSERT INTO `order` (customer_id, address_id, voucher_id, total, status) VALUES
(1,1,1,200000,'PENDING'),
(2,2,2,150000,'PAID'),
(3,3,3,400000,'PROCESSING'),
(4,4,4,500000,'SHIPPED');

INSERT INTO `order` (customer_id, address_id, voucher_id, total, status, placed_at)
VALUES
-- Tháng 1
(1,1,1,300000,'PAID','2025-01-15'),
(2,2,2,450000,'SHIPPED','2025-01-20'),
-- Tháng 2
(3,3,3,200000,'PENDING','2025-02-10'),
(4,4,4,350000,'PAID','2025-02-18'),
-- Tháng 3
(1,1,5,600000,'SHIPPED','2025-03-05'),
(2,2,1,250000,'PAID','2025-03-22'),
-- Tháng 4
(3,3,2,500000,'PROCESSING','2025-04-02'),
(4,4,3,700000,'PAID','2025-04-28'),
-- Tháng 5
(1,1,4,800000,'CANCELLED','2025-05-12'),
(2,2,5,900000,'PAID','2025-05-25'),
-- Tháng 6
(3,3,1,400000,'PAID','2025-06-09'),
(4,4,2,300000,'SHIPPED','2025-06-19'),
-- Tháng 7
(1,1,3,650000,'PROCESSING','2025-07-11'),
(2,2,4,500000,'PAID','2025-07-27'),
-- Tháng 8
(3,3,5,750000,'PAID','2025-08-06'),
(4,4,1,850000,'REFUNDED','2025-08-21');


INSERT INTO order_item (order_id, item_id, quantity, pre_discount_price, total_price_cents) VALUES
(1,1,2,100000,200000),
(2,2,1,150000,150000),
(3,3,4,100000,400000),
(4,4,2,250000,500000);

INSERT INTO order_item (order_id, item_id, quantity, pre_discount_price, total_price_cents) VALUES
-- Order 5–6 (Tháng 1)
(5,5,2,150000,300000),
(6,2,3,150000,450000),

-- Order 7–8 (Tháng 2)
(7,3,2,100000,200000),
(8,4,2,175000,350000),

-- Order 9–10 (Tháng 3)
(9,1,3,200000,600000),
(10,2,1,250000,250000),

-- Order 11–12 (Tháng 4)
(11,5,2,250000,500000),
(12,6,2,350000,700000),

-- Order 13–14 (Tháng 5)
(13,7,4,200000,800000),
(14,8,3,300000,900000),

-- Order 15–16 (Tháng 6)
(15,3,4,100000,400000),
(16,4,1,300000,300000),

-- Order 17–18 (Tháng 7)
(17,1,2,325000,650000),
(18,2,2,250000,500000),

-- Order 19–20 (Tháng 8)
(19,9,3,250000,750000),
(20,10,2,425000,850000);


INSERT INTO voucher_customer_item (voucher_id, customer_id, item_id) VALUES
(1,1,1),
(2,2,2),
(3,3,3),
(4,4,4);

INSERT INTO payment (order_id, platform, payment_method, status) VALUES
(1,'VNPAY','Cash','Initiated'),
(2,'Momo','Transfer','Complete'),
(3,'VNPAY','Cash','Failed'),
(4,'Momo','Cash','Refunded');

INSERT INTO review (item_id, customer_id, rating, comment) VALUES
(1,1,5,'Sản phẩm tốt'),
(2,2,4,'Rất hài lòng'),
(3,3,3,'Bình thường'),
(4,4,5,'Tuyệt vời');

INSERT INTO session (session_id, account_id, token, end_time) VALUES
('S1','ACC007','token1','2025-09-22 23:59:59'),
('S2','ACC008','token2','2025-09-22 23:59:59'),
('S3','ACC009','token3','2025-09-22 23:59:59'),
('S4','ACC010','token4','2025-09-22 23:59:59'),
('S5','ACC006','token5','2025-09-22 23:59:59'),
('S6','ACC001','token6','2025-09-22 23:59:59'),
('S7','ACC002','token7','2025-09-22 23:59:59');
