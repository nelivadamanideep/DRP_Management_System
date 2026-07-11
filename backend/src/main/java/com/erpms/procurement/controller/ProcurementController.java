package com.erpms.procurement.controller;

import com.erpms.procurement.dto.*;
import com.erpms.procurement.service.ProcurementService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/procurement")
@Tag(name = "Procurement", description = "Purchase requests, purchase orders and invoices")
@SecurityRequirement(name = "bearerAuth")
public class ProcurementController {

    private final ProcurementService service;

    public ProcurementController(ProcurementService service) {
        this.service = service;
    }

    // ---- Purchase requests ----------------------------------------------

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseRequestResponse create(@Valid @RequestBody PurchaseRequestRequest r) {
        return service.createRequest(r);
    }

    @PostMapping("/requests/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','FINANCE_OFFICER','PROJECT_DIRECTOR','DEPARTMENT_HEAD')")
    public PurchaseRequestResponse approve(@PathVariable String id, @RequestBody(required = false) Map<String, String> body) {
        return service.approveRequest(id, body == null ? null : body.get("comments"));
    }

    @PostMapping("/requests/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','FINANCE_OFFICER','PROJECT_DIRECTOR','DEPARTMENT_HEAD')")
    public PurchaseRequestResponse reject(@PathVariable String id, @RequestBody(required = false) Map<String, String> body) {
        return service.rejectRequest(id, body == null ? null : body.get("comments"));
    }

    @GetMapping("/requests")
    public List<PurchaseRequestResponse> listRequests(@RequestParam(required = false) String status) {
        return service.listRequests(status);
    }

    // ---- Purchase orders ------------------------------------------------

    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','PROCUREMENT_OFFICER')")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrderResponse createOrder(@Valid @RequestBody PurchaseOrderRequest r) {
        return service.createOrder(r);
    }

    @GetMapping("/orders")
    public List<PurchaseOrderResponse> listOrders(@RequestParam(required = false) String status) {
        return service.listOrders(status);
    }

    // ---- Invoices -------------------------------------------------------

    @PostMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','FINANCE_OFFICER','PROCUREMENT_OFFICER')")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponse createInvoice(@Valid @RequestBody InvoiceRequest r) {
        return service.createInvoice(r);
    }

    @PostMapping("/invoices/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','FINANCE_OFFICER')")
    public InvoiceResponse markInvoicePaid(@PathVariable String id) {
        return service.markInvoicePaid(id);
    }

    @GetMapping("/invoices")
    public List<InvoiceResponse> listInvoices() {
        return service.listInvoices();
    }
}
