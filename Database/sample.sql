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

('MANAGER001','manager001@gmail.com','Hoàng Văn An','$2a$12$ZDQfHwK49S1Hy.pE5El.j.KTGNDoeM0xlGZ05FT2EkUi8h8mLpc2m','0912345676','2000-01-01','Nam'),

('CUSTOMER001','customer001@gmail.com','Nguyễn Thị Bích','$2a$12$ROGt9ALsd1oTUZIsSSI9Wun1e.fxzrTl3r/nkd7/tq6Mv22CnhQt2','0912345677','2001-10-02','Nữ'),
('CUSTOMER002','customer002@gmail.com','Trần Văn Cường','$2a$12$w8nVbSSvZet/7SJRJgJmGeC3WTWT115THkI2ycXxquXI..ijOddhW','0912345678','2000-07-07','Nam'),
('CUSTOMER003','customer003@gmail.com','Lê Thị Diệp','$2a$12$HaEmsd/qr0XqKlUl3f1KZO1iPF8zKpnpUa0lXsOD/07HZIV6piH9K','0912345679','2002-08-08','Nữ'),
('CUSTOMER004','customer004@gmail.com','Phạm Văn Dũng','$2a$12$aStM5QsHM7a/ajcPNGva0OE/7mAMS4jg/2.oVNCKjotTAl/YRUtDy','0912345680','2001-09-09','Nam');

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
(1,'Hà Nội','Đống Đa','Số 12, Đường Láng','0912345677',1),
(2,'Hà Nội','Cầu Giấy','Số 25, Trần Thái Tông','0912345678',0),
(3,'Hải Phòng','Lê Chân','Số 5, Tô Hiệu','0912345679',1),
(4,'Đà Nẵng','Hải Châu','Số 9, Bạch Đằng','0912345680',0);

INSERT INTO voucher (code, description, discount_percent, max_uses, start_date, end_date) VALUES
('VOUCHER1','Giảm 10%',10,100,'2025-09-01','2025-12-31'),
('VOUCHER2','Giảm 15%',15,50,'2025-09-01','2025-12-31'),
('VOUCHER3','Giảm 20%',20,30,'2025-09-01','2025-12-31'),
('VOUCHER4','Giảm 5%',5,200,'2025-09-01','2025-12-31'),
('VOUCHER5','Giảm 25%',25,10,'2025-09-01','2025-12-31'),
('VOUCHER6','Giảm 10%',10,100,'2025-09-01','2025-12-31'),
('VOUCHER7','Giảm 15%',15,50,'2025-09-01','2025-12-31'),
('VOUCHER8','Giảm 20%',20,30,'2025-09-01','2025-12-31'),
('VOUCHER9','Giảm 5%',5,200,'2025-09-01','2025-12-31'),
('VOUCHER10','Giảm 25%',25,10,'2025-09-01','2025-12-31');

-- Danh mục cha
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

-- Danh mục con
INSERT INTO category (name, parent_id, slug)
SELECT 'Sữa rửa mặt', category_id, 'sua-rua-mat' FROM category WHERE slug='skincare';
INSERT INTO category (name, parent_id, slug)
SELECT 'Kem dưỡng da', category_id, 'kem-duong-da' FROM category WHERE slug='skincare';
INSERT INTO category (name, parent_id, slug)
SELECT 'Tinh chất serum', category_id, 'serum' FROM category WHERE slug='skincare';

INSERT INTO category (name, parent_id, slug)
SELECT 'Son môi', category_id, 'son-moi' FROM category WHERE slug='makeup';
INSERT INTO category (name, parent_id, slug)
SELECT 'Kem nền', category_id, 'kem-nen' FROM category WHERE slug='makeup';
INSERT INTO category (name, parent_id, slug)
SELECT 'Mascara', category_id, 'mascara' FROM category WHERE slug='makeup';

INSERT INTO category (name, parent_id, slug)
SELECT 'Eau de Parfum', category_id, 'eau-de-parfum' FROM category WHERE slug='perfume';
INSERT INTO category (name, parent_id, slug)
SELECT 'Eau de Toilette', category_id, 'eau-de-toilette' FROM category WHERE slug='perfume';

INSERT INTO category (name, parent_id, slug)
SELECT 'Dầu gội', category_id, 'dau-goi' FROM category WHERE slug='haircare';
INSERT INTO category (name, parent_id, slug)
SELECT 'Dầu xả', category_id, 'dau-xa' FROM category WHERE slug='haircare';
INSERT INTO category (name, parent_id, slug)
SELECT 'Dầu dưỡng tóc', category_id, 'dau-duong-toc' FROM category WHERE slug='haircare';

-- Items
INSERT INTO item (name, description, color, ingredient, price, category_id) VALUES
('Sữa rửa mặt dịu nhẹ', 'Làm sạch sâu mà không gây khô da', 'Trắng', 'Tinh dầu tràm trà', 100000, 1),
('Kem dưỡng ẩm ban đêm', 'Dưỡng ẩm giúp da mềm mịn', 'Xanh dương', 'Hyaluronic Acid', 150000, 2),
('Serum vitamin C', 'Làm sáng da và mờ thâm', 'Trong suốt', 'Vitamin C 12%', 200000, 3),
('Dầu gội phục hồi', 'Phục hồi tóc hư tổn', 'Vàng nhạt', 'Keratin', 250000, 4),
('Sữa tắm hương hoa', 'Làm sạch và tạo hương thơm', 'Trắng', 'Tinh dầu oải hương', 300000, 5),
('Nước hoa nam Cool Fresh', 'Hương thơm nam tính, tươi mát', 'Xanh biển', 'Hương cam bergamot', 120000, 6),
('Nước hoa nữ Sweet Rose', 'Hương hoa hồng nhẹ nhàng', 'Hồng', 'Tinh chất hoa hồng', 180000, 7),
('Dầu dưỡng tóc Argan', 'Giúp tóc mềm mượt', 'Vàng', 'Dầu Argan', 220000, 8),
('Son môi đỏ', 'Màu đỏ quyến rũ', 'Đỏ', 'Vitamin E', 270000, 9),
('Kem nền mịn lì', 'Che phủ tốt, lâu trôi', 'Be', 'Khoáng chất thiên nhiên', 320000, 10);

INSERT INTO item_image (item_id, alt, is_primary) VALUES
(1,'Hình sản phẩm 1',1),
(2,'Hình sản phẩm 2',1),
(3,'Hình sản phẩm 3',1),
(4,'Hình sản phẩm 4',1),
(5,'Hình sản phẩm 5',1),
(6,'Hình sản phẩm 6',1),
(7,'Hình sản phẩm 7',1),
(8,'Hình sản phẩm 8',1),
(9,'Hình sản phẩm 9',1),
(10,'Hình sản phẩm 10',1);

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

-- Orders (giữ nguyên logic mẫu)
INSERT INTO `order` (customer_id, address_id, voucher_id, total, status) VALUES
(1,1,1,200000,'PENDING'),
(2,2,2,150000,'PAID'),
(3,3,3,400000,'PROCESSING'),
(4,4,4,500000,'SHIPPED');

-- Đơn hàng theo tháng
INSERT INTO `order` (customer_id, address_id, voucher_id, total, status, placed_at)
VALUES
(1,1,1,300000,'PAID','2025-01-15'),
(2,2,2,450000,'SHIPPED','2025-01-20'),
(3,3,3,200000,'PENDING','2025-02-10'),
(4,4,4,350000,'PAID','2025-02-18'),
(1,1,5,600000,'SHIPPED','2025-03-05'),
(2,2,1,250000,'PAID','2025-03-22'),
(3,3,2,500000,'PROCESSING','2025-04-02'),
(4,4,3,700000,'PAID','2025-04-28'),
(1,1,4,800000,'CANCELLED','2025-05-12'),
(2,2,5,900000,'PAID','2025-05-25'),
(3,3,1,400000,'PAID','2025-06-09'),
(4,4,2,300000,'SHIPPED','2025-06-19'),
(1,1,3,650000,'PROCESSING','2025-07-11'),
(2,2,4,500000,'PAID','2025-07-27'),
(3,3,5,750000,'PAID','2025-08-06'),
(4,4,1,850000,'REFUNDED','2025-08-21');

INSERT INTO order_item (order_id, item_id, quantity, pre_discount_price, total_price_cents) VALUES
(1,1,2,100000,200000),
(2,2,1,150000,150000),
(3,3,4,100000,400000),
(4,4,2,250000,500000);

INSERT INTO order_item (order_id, item_id, quantity, pre_discount_price, total_price_cents) VALUES
(5,5,2,150000,300000),
(6,2,3,150000,450000),
(7,3,2,100000,200000),
(8,4,2,175000,350000),
(9,1,3,200000,600000),
(10,2,1,250000,250000),
(11,5,2,250000,500000),
(12,6,2,350000,700000),
(13,7,4,200000,800000),
(14,8,3,300000,900000),
(15,3,4,100000,400000),
(16,4,1,300000,300000),
(17,1,2,325000,650000),
(18,2,2,250000,500000),
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
(1,1,5,'Sản phẩm rất tốt, đáng mua'),
(2,2,4,'Hài lòng, giao hàng nhanh'),
(3,3,3,'Chất lượng ổn, giá được'),
(4,4,5,'Rất tuyệt vời, mùi thơm lâu');

INSERT INTO session (session_id, account_id, token, end_time, start_time) VALUES
('S1','ACC007','token1','2025-09-22 23:59:59', '2025-09-21 23:59:59'),
('S2','ACC008','token2','2025-09-22 23:59:59', '2025-09-21 23:59:59'),
('S3','ACC009','token3','2025-09-22 23:59:59', '2025-09-21 23:59:59'),
('S4','ACC010','token4','2025-09-22 23:59:59', '2025-09-21 23:59:59'),
('S5','ACC006','token5','2025-09-22 23:59:59', '2025-09-21 23:59:59'),
('S6','ACC001','token6','2025-09-22 23:59:59', '2025-09-21 23:59:59'),
('S7','ACC002','token7','2025-09-22 23:59:59', '2025-09-21 23:59:59');