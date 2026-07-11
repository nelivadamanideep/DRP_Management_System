package com.erpms.inventory.service;

import com.erpms.common.exception.BusinessRuleException;
import com.erpms.common.exception.ResourceNotFoundException;
import com.erpms.common.security.SecurityUtils;
import com.erpms.inventory.dto.*;
import com.erpms.inventory.entity.*;
import com.erpms.inventory.repository.*;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final WarehouseRepository warehouseRepository;
    private final InventoryItemRepository itemRepository;
    private final StockMovementRepository movementRepository;
    private final SupplierRepository supplierRepository;

    public InventoryService(WarehouseRepository w, InventoryItemRepository i,
                            StockMovementRepository m, SupplierRepository s) {
        this.warehouseRepository = w;
        this.itemRepository = i;
        this.movementRepository = m;
        this.supplierRepository = s;
    }

    // ---- Warehouses -----------------------------------------------------

    @Transactional
    public WarehouseResponse createWarehouse(WarehouseRequest req) {
        if (warehouseRepository.existsByCodeIgnoreCase(req.code())) {
            throw new BusinessRuleException("Warehouse code already exists");
        }
        WarehouseEntity w = new WarehouseEntity();
        w.setCode(req.code().trim().toUpperCase());
        w.setName(req.name().trim());
        w.setLocation(req.location());
        w.setManagerUserId(req.managerUserId());
        w.setActive(req.active() == null || req.active());
        return toWarehouseResponse(warehouseRepository.save(w));
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponse> findAllWarehouses() {
        return warehouseRepository.findAll().stream().map(this::toWarehouseResponse).toList();
    }

    // ---- Items ----------------------------------------------------------

    @Transactional
    public InventoryItemResponse createItem(InventoryItemRequest req) {
        if (itemRepository.existsBySkuIgnoreCase(req.sku())) {
            throw new BusinessRuleException("SKU already exists");
        }
        InventoryItemEntity item = new InventoryItemEntity();
        applyItem(item, req);
        item.setSku(req.sku().trim().toUpperCase());
        return toItemResponse(itemRepository.save(item));
    }

    @Transactional
    public InventoryItemResponse updateItem(String id, InventoryItemRequest req) {
        InventoryItemEntity item = itemRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Item", id));
        applyItem(item, req);
        return toItemResponse(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> findAllItems() {
        return itemRepository.findAll().stream().map(this::toItemResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> findLowStock() {
        return itemRepository.findLowStock().stream().map(this::toItemResponse).toList();
    }

    // ---- Stock movements ------------------------------------------------

    @Transactional
    public StockMovementResponse moveStock(StockMovementRequest req) {
        InventoryItemEntity item = itemRepository.findById(req.itemId())
                .orElseThrow(() -> ResourceNotFoundException.of("Item", req.itemId()));

        String dir = req.direction().trim().toUpperCase();
        BigDecimal q = req.quantity();
        if (q == null || q.signum() <= 0) throw new BusinessRuleException("Quantity must be positive");

        BigDecimal newStock;
        switch (dir) {
            case "IN"     -> newStock = item.getStockQuantity().add(q);
            case "OUT"    -> {
                if (item.getStockQuantity().compareTo(q) < 0) {
                    throw new BusinessRuleException("Insufficient stock");
                }
                newStock = item.getStockQuantity().subtract(q);
            }
            case "ADJUST" -> newStock = q; // set absolute
            default -> throw new BusinessRuleException("Unknown movement direction: " + dir);
        }
        item.setStockQuantity(newStock);
        itemRepository.save(item);

        StockMovementEntity movement = new StockMovementEntity();
        movement.setItemId(req.itemId());
        movement.setDirection(dir);
        movement.setQuantity(q);
        movement.setReason(req.reason());
        movement.setReferenceId(req.referenceId());
        movement.setPerformedByUserId(SecurityUtils.currentUserIdOrNull());
        return toMovementResponse(movementRepository.save(movement));
    }

    @Transactional(readOnly = true)
    public List<StockMovementResponse> listMovements(String itemId) {
        return movementRepository.findByItemIdOrderByCreatedAtDesc(itemId)
                .stream().map(this::toMovementResponse).toList();
    }

    // ---- Suppliers ------------------------------------------------------

    @Transactional
    public SupplierResponse createSupplier(SupplierRequest req) {
        SupplierEntity s = new SupplierEntity();
        applySupplier(s, req);
        return toSupplierResponse(supplierRepository.save(s));
    }

    @Transactional
    public SupplierResponse updateSupplier(String id, SupplierRequest req) {
        SupplierEntity s = supplierRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Supplier", id));
        applySupplier(s, req);
        return toSupplierResponse(supplierRepository.save(s));
    }

    @Transactional(readOnly = true)
    public List<SupplierResponse> findAllSuppliers() {
        return supplierRepository.findAll().stream().map(this::toSupplierResponse).toList();
    }

    // ---- Mappers --------------------------------------------------------

    private void applyItem(InventoryItemEntity item, InventoryItemRequest req) {
        item.setName(req.name().trim());
        item.setDescription(req.description());
        item.setCategory(req.category());
        item.setUnit(req.unit());
        item.setWarehouseId(req.warehouseId());
        item.setStockQuantity(req.stockQuantity());
        item.setReorderLevel(req.reorderLevel());
        item.setUnitCost(req.unitCost());
        item.setSupplierId(req.supplierId());
    }

    private void applySupplier(SupplierEntity s, SupplierRequest req) {
        s.setName(req.name().trim());
        s.setContactEmail(req.contactEmail());
        s.setContactPhone(req.contactPhone());
        s.setAddress(req.address());
        s.setGstNumber(req.gstNumber());
        s.setActive(req.active() == null || req.active());
    }

    private WarehouseResponse toWarehouseResponse(WarehouseEntity w) {
        return new WarehouseResponse(w.getId(), w.getCode(), w.getName(), w.getLocation(),
                w.getManagerUserId(), w.isActive());
    }

    private InventoryItemResponse toItemResponse(InventoryItemEntity i) {
        boolean low = i.getStockQuantity().compareTo(i.getReorderLevel()) <= 0;
        return new InventoryItemResponse(i.getId(), i.getSku(), i.getName(), i.getDescription(),
                i.getCategory(), i.getUnit(), i.getWarehouseId(), i.getStockQuantity(),
                i.getReorderLevel(), i.getUnitCost(), i.getSupplierId(), low);
    }

    private StockMovementResponse toMovementResponse(StockMovementEntity m) {
        return new StockMovementResponse(m.getId(), m.getItemId(), m.getDirection(), m.getQuantity(),
                m.getReason(), m.getReferenceId(), m.getPerformedByUserId(), m.getCreatedAt());
    }

    private SupplierResponse toSupplierResponse(SupplierEntity s) {
        return new SupplierResponse(s.getId(), s.getName(), s.getContactEmail(), s.getContactPhone(),
                s.getAddress(), s.getGstNumber(), s.isActive());
    }
}
