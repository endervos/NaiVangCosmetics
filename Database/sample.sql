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
(1,1,1,200000,'Pending'),
(2,2,2,150000,'Paid'),
(3,3,3,400000,'Processing'),
(4,4,4,500000,'Shipped');

INSERT INTO order_item (order_id, item_id, quantity, pre_discount_price, total_price_cents) VALUES
(1,1,2,100000,200000),
(2,2,1,150000,150000),
(3,3,4,100000,400000),
(4,4,2,250000,500000);

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
