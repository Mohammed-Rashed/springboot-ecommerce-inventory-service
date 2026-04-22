package com.rashed.inventoryservice.inventory.service;


import com.rashed.inventoryservice.inventory.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.rashed.inventoryservice.common.exception.ConflictException;
import com.rashed.inventoryservice.common.exception.NotFoundException;
import com.rashed.inventoryservice.inventory.entity.InventoryItem;
import com.rashed.inventoryservice.inventory.repository.InventoryRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryResponse create(CreateInventoryRequest request) {
        if (inventoryRepository.existsByProductId(request.productId())) {
            throw new ConflictException("Inventory already exists for productId: " + request.productId());
        }

        if (inventoryRepository.existsBySku(request.sku())) {
            throw new ConflictException("Inventory already exists for sku: " + request.sku());
        }

        InventoryItem item = InventoryItem.builder()
                .productId(request.productId())
                .sku(request.sku())
                .availableQuantity(request.availableQuantity())
                .reservedQuantity(0)
                .build();

        InventoryItem savedItem = inventoryRepository.save(item);
        return mapToResponse(savedItem);
    }

    @Transactional
    public InventoryResponse update(Long productId, UpdateInventoryRequest request) {
        InventoryItem item = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found for productId: " + productId));

//        if (inventoryRepository.existsBySkuAndProductIdNot(request.sku(), productId)) {
//            throw new ConflictException("Inventory already exists for sku: " + request.sku());
//        }

//        item.setSku(request.sku());
        item.setAvailableQuantity(request.availableQuantity());

        InventoryItem updatedItem = inventoryRepository.save(item);
        return mapToResponse(updatedItem);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getByProductId(Long productId) {
        InventoryItem item = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found for productId: " + productId));

        return mapToResponse(item);
    }

    @Transactional(readOnly = true)
    public CheckAvailabilityResponse checkAvailability(CheckAvailabilityRequest request) {
        InventoryItem item = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() -> new NotFoundException("Inventory not found for productId: " + request.productId()));

        boolean available = item.getAvailableQuantity() >= request.requestedQuantity();

        return new CheckAvailabilityResponse(
                item.getProductId(),
                request.requestedQuantity(),
                item.getAvailableQuantity(),
                available
        );
    }

    private InventoryResponse mapToResponse(InventoryItem item) {
        return new InventoryResponse(
                item.getId(),
                item.getProductId(),
                item.getSku(),
                item.getAvailableQuantity(),
                item.getReservedQuantity(),
                item.getUpdatedAt()
        );
    }



    @Transactional
    public StockOperationResponse reserve(ReserveStockRequest request) {
        InventoryItem item = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() -> new NotFoundException("Inventory not found for productId: " + request.productId()));

        int updated = inventoryRepository.reserveStock(request.productId(), request.quantity());

        if (updated == 0) {
            throw new ConflictException("Insufficient available stock for productId: " + request.productId());
        }

        InventoryItem updatedItem = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() -> new NotFoundException("Inventory not found for productId: " + request.productId()));

        return mapToStockOperationResponse(updatedItem, "Stock reserved successfully");
    }

    @Transactional
    public StockOperationResponse release(ReleaseStockRequest request) {
        InventoryItem item = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() -> new NotFoundException("Inventory not found for productId: " + request.productId()));

        int updated = inventoryRepository.releaseStock(request.productId(), request.quantity());

        if (updated == 0) {
            throw new ConflictException("Insufficient reserved stock for productId: " + request.productId());
        }

        InventoryItem updatedItem = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() -> new NotFoundException("Inventory not found for productId: " + request.productId()));

        return mapToStockOperationResponse(updatedItem, "Stock released successfully");
    }

    @Transactional
    public StockOperationResponse deduct(DeductStockRequest request) {
        InventoryItem item = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() -> new NotFoundException("Inventory not found for productId: " + request.productId()));

        int updated = inventoryRepository.deductStock(request.productId(), request.quantity());

        if (updated == 0) {
            throw new ConflictException("Insufficient reserved stock to deduct for productId: " + request.productId());
        }

        InventoryItem updatedItem = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() -> new NotFoundException("Inventory not found for productId: " + request.productId()));

        return mapToStockOperationResponse(updatedItem, "Stock deducted successfully");
    }

    private StockOperationResponse mapToStockOperationResponse(InventoryItem item, String message) {
        return new StockOperationResponse(
                item.getProductId(),
                item.getSku(),
                item.getAvailableQuantity(),
                item.getReservedQuantity(),
                message,
                item.getUpdatedAt()
        );
    }

}
