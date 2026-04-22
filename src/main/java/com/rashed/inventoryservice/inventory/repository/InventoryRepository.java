package com.rashed.inventoryservice.inventory.repository;

import com.rashed.inventoryservice.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItem,Long> {
    Optional<InventoryItem> findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    boolean existsBySku(String sku);

    boolean existsBySkuAndProductIdNot(String sku, Long productId);



    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update InventoryItem i
            set i.availableQuantity = i.availableQuantity - :quantity,
                i.reservedQuantity = i.reservedQuantity + :quantity,
                i.updatedAt = CURRENT_TIMESTAMP
            where i.productId = :productId
              and i.availableQuantity >= :quantity
            """)
    int reserveStock(Long productId, Integer quantity);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update InventoryItem i
            set i.availableQuantity = i.availableQuantity + :quantity,
                i.reservedQuantity = i.reservedQuantity - :quantity,
                i.updatedAt = CURRENT_TIMESTAMP
            where i.productId = :productId
              and i.reservedQuantity >= :quantity
            """)
    int releaseStock(Long productId, Integer quantity);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update InventoryItem i
            set i.reservedQuantity = i.reservedQuantity - :quantity,
                i.updatedAt = CURRENT_TIMESTAMP
            where i.productId = :productId
              and i.reservedQuantity >= :quantity
            """)
    int deductStock(Long productId, Integer quantity);
}
