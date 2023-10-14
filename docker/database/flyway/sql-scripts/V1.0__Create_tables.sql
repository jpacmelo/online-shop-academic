create table if not exists PRODUCT
(
    id                              BIGSERIAL PRIMARY KEY,
    name                            VARCHAR(256),
    description                     VARCHAR(512),
    price                           NUMERIC
);
create table if not exists INVENTORY (
    id                              BIGSERIAL PRIMARY KEY,
    quantity                        INTEGER,
    sku_code                        VARCHAR(255)
);
INSERT INTO INVENTORY (quantity, sku_code) VALUES (100, 'iphone_13');
INSERT INTO INVENTORY (quantity, sku_code) VALUES (0, 'iphone_13_red');
INSERT INTO PRODUCT (name, description, price) VALUES ('iphone_13', 'iphone 13 black', 1000);
INSERT INTO PRODUCT (name, description, price) VALUES ('iphone_13_red', 'iphone 13 black', 1000)