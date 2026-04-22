package com.rashed.inventoryservice.inventory.repository;

import com.rashed.inventoryservice.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItem,Long> {
    Optional<InventoryItem> findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    boolean existsBySku(String sku);

    boolean existsBySkuAndProductIdNot(String sku, Long productId);

}
