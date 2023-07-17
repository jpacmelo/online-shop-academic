create table if not exists PRODUCT
(
    id                              BIGSERIAL PRIMARY KEY,
    name                            VARCHAR(256),
    description                     VARCHAR(512),
    price                           NUMERIC
);