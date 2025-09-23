USE COSMETICS;

INSERT INTO roles (name, description) VALUES
('Customer','Khách hàng mua sắm'),
('Manager','Người quản lý cửa hàng'),
('Admin','Quản trị hệ thống');

INSERT INTO users (user_id, email, fullname,password, phone_number, birthday, gender) VALUES
('U1','tranthianhnguyet259@gmail.com','Nguyen Van A','pass1','0912345671','2000-01-01','Nam'),
('U2','user2@gmail.com','Tran Thi B','pass2','0912345672','2001-02-02','Nữ'),
('U3','user3@gmail.com','Le Van C','pass3','0912345673','2002-03-03','Nam'),
('U4','user4@gmail.com','Pham Thi D','pass4','0912345674','2000-04-04','Nữ'),
('U5','user5@gmail.com','Hoang Van E','pass5','0912345675','1999-05-05','Nam'),
('U6','nguyetanmun@gmail.com','Nguyen Thi F','pass6','0912345676','2001-06-06','Nữ'),
('U7','user7@gmail.com','Tran Van G','pass7','0912345677','2000-07-07','Nam'),
('U8','user8@gmail.com','Le Thi H','pass8','0912345678','2002-08-08','Nữ'),
('U9','n22dcat040@student.ptithcm.edu.vn','Pham Van I','pass9','0912345679','2001-09-09','Nam'),
('U10','user10@gmail.com','Hoang Thi J','pass10','0912345680','2000-10-10','Nữ');

INSERT INTO accounts (account_id, user_id, role_id, isActive) VALUES
('CU1','U1',1,1),
('CU2','U2',1,1),
('CU3','U3',1,1),
('CU4','U4',1,1),
('CU5','U5',1,1),
('MN1','U6',2,1),
('MN2','U7',2,1),
('MN3','U8',2,1),
('AD1','U9',3,1),
('AD2','U10',3,1);

INSERT INTO customer_types (customer_type_name) VALUES
('VIP'),
('Quen'),
('Vang lai');

INSERT INTO customers (user_id, customer_type_id) VALUES
('U1',1),
('U2',2),
('U3',3),
('U4',1),
('U5',2);

INSERT INTO managers (user_id) VALUES
('U6'),
('U7'),
('U8');

INSERT INTO admins (user_id) VALUES
('U9'),
('U10');

INSERT INTO addresses (customer_id, city, district, street, phone_number, id_address_default) VALUES
(1,'Ha Noi','Dong Da','Duong A','0912345671',1),
(2,'Ha Noi','Cau Giay','Duong B','0912345672',0),
(3,'Hai Phong','Le Chan','Duong C','0912345673',1),
(4,'Da Nang','Hai Chau','Duong D','0912345674',0),
(5,'HCM','1','Duong E','0912345675',1);

INSERT INTO vouchers (code, description, discount_percent, max_uses, start_date, end_date) VALUES
('VOUCHER1','Giam 10%','10',100,'2025-09-01','2025-12-31'),
('VOUCHER2','Giam 15%','15',50,'2025-09-01','2025-12-31'),
('VOUCHER3','Giam 20%','20',30,'2025-09-01','2025-12-31'),
('VOUCHER4','Giam 5%','5',200,'2025-09-01','2025-12-31'),
('VOUCHER5','Giam 25%','25',10,'2025-09-01','2025-12-31'),
('VOUCHER6','Giam 10%','10',100,'2025-09-01','2025-12-31'),
('VOUCHER7','Giam 15%','15',50,'2025-09-01','2025-12-31'),
('VOUCHER8','Giam 20%','20',30,'2025-09-01','2025-12-31'),
('VOUCHER9','Giam 5%','5',200,'2025-09-01','2025-12-31'),
('VOUCHER10','Giam 25%','25',10,'2025-09-01','2025-12-31');

INSERT INTO categories (name, slug) VALUES
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

INSERT INTO items (name, description, color, ingredient, price, category_id) VALUES
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

INSERT INTO item_images (item_id, alt, is_primary) VALUES
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

INSERT INTO carts (account_id) VALUES
('CU1'),
('CU2'),
('CU3'),
('CU4'),
('CU5'),
('MN1'),
('MN2'),
('MN3'),
('AD1'),
('AD2');

INSERT INTO cart_items (cart_id, item_id, quantity) VALUES
(1,1,2),
(2,2,1),
(3,3,4),
(4,4,2),
(5,5,3),
(6,6,1),
(7,7,2),
(8,8,1),
(9,9,5),
(10,10,2);

INSERT INTO orders (customer_id, address_id, voucher_id, total, status) VALUES
(1,1,1,200000,'pending'),
(2,2,2,150000,'paid'),
(3,3,3,400000,'processing'),
(4,4,4,500000,'shipped'),
(5,5,5,600000,'completed'),
(1,1,6,220000,'cancelled'),
(2,2,7,180000,'refunded'),
(3,3,8,450000,'paid'),
(4,4,9,520000,'pending'),
(5,5,10,610000,'processing');

INSERT INTO order_items (order_id, item_id, quantity, pre_discount_price, total_price_cents) VALUES
(1,1,2,100000,200000),
(2,2,1,150000,150000),
(3,3,4,100000,400000),
(4,4,2,250000,500000),
(5,5,3,200000,600000),
(6,1,2,110000,220000),
(7,2,1,180000,180000),
(8,3,3,150000,450000),
(9,4,2,260000,520000),
(10,5,3,203000,610000);

INSERT INTO voucher_customer_items (voucher_id, customer_id, item_id) VALUES
(1,1,1),
(2,2,2),
(3,3,3),
(4,4,4),
(5,5,5),
(6,1,1),
(7,2,2),
(8,3,3),
(9,4,4),
(10,5,5);

INSERT INTO payments (order_id, platform, payment_method, status) VALUES
(1,'VNPAY','Tien mat','initiated'),
(2,'Momo','Chuyen khoan','complete'),
(3,'VNPAY','Tien mat','failed'),
(4,'Momo','Tien mat','refunded'),
(5,'VNPAY','Chuyen khoan','complete'),
(6,'Momo','Tien mat','initiated'),
(7,'VNPAY','Chuyen khoan','failed'),
(8,'Momo','Tien mat','complete'),
(9,'VNPAY','Tien mat','refunded'),
(10,'Momo','Chuyen khoan','complete');


INSERT INTO reviews (item_id, customer_id, rating, comment) VALUES
(1,1,5,'Sản phẩm tốt'),
(2,2,4,'Rất hài lòng'),
(3,3,3,'Bình thường'),
(4,4,5,'Tuyệt vời'),
(5,5,2,'Không thích'),
(6,1,4,'Ổn'),
(7,2,3,'Chấp nhận được'),
(8,3,5,'Xuất sắc'),
(9,4,4,'Hài lòng'),
(10,5,5,'Rất tốt');


INSERT INTO sessions (session_id, account_id, token, end_time) VALUES
('S1','CU1','token1','2025-09-22 23:59:59'),
('S2','CU2','token2','2025-09-22 23:59:59'),
('S3','CU3','token3','2025-09-22 23:59:59'),
('S4','CU4','token4','2025-09-22 23:59:59'),
('S5','CU5','token5','2025-09-22 23:59:59'),
('S6','MN1','token6','2025-09-22 23:59:59'),
('S7','MN2','token7','2025-09-22 23:59:59'),
('S8','MN3','token8','2025-09-22 23:59:59'),
('S9','AD1','token9','2025-09-22 23:59:59'),
('S10','AD2','token10','2025-09-22 23:59:59');



