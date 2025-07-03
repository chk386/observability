-- 기존 데이터 삭제
DELETE FROM products;

-- 시퀀스 초기화 (PostgreSQL의 경우)
ALTER SEQUENCE products_product_id_seq RESTART WITH 1;

-- 초기 데이터 삽입
INSERT INTO products (product_name, view_count, stock_quantity, product_price, created_by, created_at, modified_by, modified_at) VALUES
('갤럭시 S24 Ultra', 1250, 45, 1299000.00, 'system', NOW(), 'system', NOW()),
('아이폰 15 Pro Max', 2100, 32, 1590000.00, 'system', NOW(), 'system', NOW()),
('맥북 프로 14인치', 890, 18, 2490000.00, 'system', NOW(), 'system', NOW()),
('에어팟 프로 2세대', 3200, 120, 329000.00, 'system', NOW(), 'system', NOW()),
('갤럭시 북 프로', 450, 25, 1890000.00, 'system', NOW(), 'system', NOW()),
('아이패드 프로 12.9', 1100, 35, 1790000.00, 'system', NOW(), 'system', NOW()),
('삼성 모니터 32인치', 670, 28, 890000.00, 'system', NOW(), 'system', NOW()),
('로지텍 MX Master 3', 540, 85, 129000.00, 'system', NOW(), 'system', NOW()),
('키보드 매직 키보드', 320, 42, 189000.00, 'system', NOW(), 'system', NOW()),
( '소니 WH-1000XM5', 780, 60, 449000.00, 'system', NOW(), 'system', NOW()),
( '갤럭시 워치 6', 920, 75, 389000.00, 'system', NOW(), 'system', NOW()),
( '애플 워치 시리즈 9', 1400, 55, 599000.00, 'system', NOW(), 'system', NOW()),
( '닌텐도 스위치 OLED', 2300, 40, 429000.00, 'system', NOW(), 'system', NOW()),
( 'PS5 디지털 에디션', 1800, 15, 549000.00, 'system', NOW(), 'system', NOW()),
( 'Xbox Series X', 950, 22, 649000.00, 'system', NOW(), 'system', NOW()),
( '아이맥 24인치', 380, 12, 1890000.00, 'system', NOW(), 'system', NOW()),
( '갤럭시 탭 S9', 610, 38, 899000.00, 'system', NOW(), 'system', NOW()),
( '서피스 프로 9', 420, 20, 1590000.00, 'system', NOW(), 'system', NOW()),
( '레이저 데스애더 V3', 290, 95, 89000.00, 'system', NOW(), 'system', NOW()),
( 'LG 그램 17인치', 350, 30, 2190000.00, 'system', NOW(), 'system', NOW());