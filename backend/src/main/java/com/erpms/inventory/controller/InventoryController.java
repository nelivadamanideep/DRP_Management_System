package com.erpms.inventory.controller;

import com.erpms.inventory.dto.*;
import com.erpms.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory", description = "Warehouses, items, stock movements and suppliers")
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    // Warehouses
    @PostMapping("/warehouses")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LABORATORY_MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseResponse createWarehouse(@Valid @RequestBody WarehouseRequest r) {
        return service.createWarehouse(r);
    }

    @GetMapping("/warehouses")
    public List<WarehouseResponse> listWarehouses() { return service.findAllWarehouses(); }

    // Items
    @PostMapping("/items")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LABORATORY_MANAGER','PROCUREMENT_OFFICER')")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryItemResponse createItem(@Valid @RequestBody InventoryItemRequest r) {
        return service.createItem(r);
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LABORATORY_MANAGER','PROCUREMENT_OFFICER')")
    public InventoryItemResponse updateItem(@PathVariable String id, @Valid @RequestBody InventoryItemRequest r) {
        return service.updateItem(id, r);
    }

    @GetMapping("/items")
    public List<InventoryItemResponse> listItems() { return service.findAllItems(); }

    @GetMapping("/items/low-stock")
    public List<InventoryItemResponse> lowStock() { return service.findLowStock(); }

    // Movements
    @PostMapping("/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovementResponse move(@Valid @RequestBody StockMovementRequest r) {
        return service.moveStock(r);
    }

    @GetMapping("/items/{id}/movements")
    public List<StockMovementResponse> listMovements(@PathVariable String id) {
        return service.listMovements(id);
    }

    // Suppliers
    @PostMapping("/suppliers")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','PROCUREMENT_OFFICER')")
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierResponse createSupplier(@Valid @RequestBody SupplierRequest r) {
        return service.createSupplier(r);
    }

    @PutMapping("/suppliers/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','PROCUREMENT_OFFICER')")
    public SupplierResponse updateSupplier(@PathVariable String id, @Valid @RequestBody SupplierRequest r) {
        return service.updateSupplier(id, r);
    }

    @GetMapping("/suppliers")
    public List<SupplierResponse> listSuppliers() { return service.findAllSuppliers(); }
}
