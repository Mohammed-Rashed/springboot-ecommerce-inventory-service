CREATE TABLE inventory_items (
                                 id BIGSERIAL PRIMARY KEY,
                                 product_id BIGINT NOT NULL,
                                 sku VARCHAR(100) NOT NULL,
                                 available_quantity INT NOT NULL DEFAULT 0,
                                 reserved_quantity INT NOT NULL DEFAULT 0,
                                 updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

                                 CONSTRAINT uk_inventory_product_id UNIQUE (product_id),
                                 CONSTRAINT uk_inventory_sku UNIQUE (sku),
                                 CONSTRAINT chk_available_quantity_non_negative CHECK (available_quantity >= 0),
                                 CONSTRAINT chk_reserved_quantity_non_negative CHECK (reserved_quantity >= 0)
);