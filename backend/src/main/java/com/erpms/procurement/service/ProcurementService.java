package com.erpms.procurement.service;

import com.erpms.common.exception.BusinessRuleException;
import com.erpms.common.exception.ResourceNotFoundException;
import com.erpms.common.security.SecurityUtils;
import com.erpms.procurement.dto.*;
import com.erpms.procurement.entity.*;
import com.erpms.procurement.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcurementService {

    private final PurchaseRequestRepository requestRepo;
    private final PurchaseOrderRepository orderRepo;
    private final InvoiceRepository invoiceRepo;

    public ProcurementService(PurchaseRequestRepository r, PurchaseOrderRepository o, InvoiceRepository i) {
        this.requestRepo = r;
        this.orderRepo = o;
        this.invoiceRepo = i;
    }

    // ---- Purchase requests ----------------------------------------------

    @Transactional
    public PurchaseRequestResponse createRequest(PurchaseRequestRequest req) {
        if (requestRepo.existsByRequestNumberIgnoreCase(req.requestNumber())) {
            throw new BusinessRuleException("Purchase request number already exists");
        }
        PurchaseRequestEntity e = new PurchaseRequestEntity();
        e.setRequestNumber(req.requestNumber().trim().toUpperCase());
        e.setTitle(req.title().trim());
        e.setJustification(req.justification());
        e.setProjectId(req.projectId());
        e.setSupplierId(req.supplierId());
        e.setEstimatedCost(req.estimatedCost());
        e.setRequestedByUserId(SecurityUtils.currentUserIdOrNull());
        e.setStatus("SUBMITTED");
        return toRequestResponse(requestRepo.save(e));
    }

    @Transactional
    public PurchaseRequestResponse approveRequest(String id, String comments) {
        PurchaseRequestEntity e = requireRequest(id);
        if (!"SUBMITTED".equals(e.getStatus())) {
            throw new BusinessRuleException("Only submitted requests can be approved");
        }
        e.setStatus("APPROVED");
        e.setApproverUserId(SecurityUtils.currentUserIdOrNull());
        e.setApproverComments(comments);
        return toRequestResponse(requestRepo.save(e));
    }

    @Transactional
    public PurchaseRequestResponse rejectRequest(String id, String comments) {
        PurchaseRequestEntity e = requireRequest(id);
        if (!"SUBMITTED".equals(e.getStatus())) {
            throw new BusinessRuleException("Only submitted requests can be rejected");
        }
        e.setStatus("REJECTED");
        e.setApproverUserId(SecurityUtils.currentUserIdOrNull());
        e.setApproverComments(comments);
        return toRequestResponse(requestRepo.save(e));
    }

    @Transactional(readOnly = true)
    public List<PurchaseRequestResponse> listRequests(String status) {
        List<PurchaseRequestEntity> data = status == null || status.isBlank()
                ? requestRepo.findAll()
                : requestRepo.findByStatus(status.trim().toUpperCase());
        return data.stream().map(this::toRequestResponse).toList();
    }

    // ---- Purchase orders ------------------------------------------------

    @Transactional
    public PurchaseOrderResponse createOrder(PurchaseOrderRequest req) {
        if (orderRepo.existsByPoNumberIgnoreCase(req.poNumber())) {
            throw new BusinessRuleException("Purchase order number already exists");
        }
        if (req.requestId() != null) {
            PurchaseRequestEntity pr = requireRequest(req.requestId());
            if (!"APPROVED".equals(pr.getStatus())) {
                throw new BusinessRuleException("Cannot raise a PO for a request that is not approved");
            }
            pr.setStatus("ORDERED");
            requestRepo.save(pr);
        }
        PurchaseOrderEntity o = new PurchaseOrderEntity();
        o.setPoNumber(req.poNumber().trim().toUpperCase());
        o.setRequestId(req.requestId());
        o.setSupplierId(req.supplierId());
        o.setIssuedByUserId(SecurityUtils.currentUserIdOrNull());
        o.setExpectedDelivery(req.expectedDelivery());
        o.setTotalAmount(req.totalAmount());
        o.setNotes(req.notes());
        o.setStatus("ISSUED");
        return toOrderResponse(orderRepo.save(o));
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> listOrders(String status) {
        List<PurchaseOrderEntity> data = status == null || status.isBlank()
                ? orderRepo.findAll() : orderRepo.findByStatus(status.trim().toUpperCase());
        return data.stream().map(this::toOrderResponse).toList();
    }

    // ---- Invoices -------------------------------------------------------

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest req) {
        if (invoiceRepo.existsByInvoiceNumberIgnoreCase(req.invoiceNumber())) {
            throw new BusinessRuleException("Invoice number already exists");
        }
        InvoiceEntity inv = new InvoiceEntity();
        inv.setInvoiceNumber(req.invoiceNumber().trim().toUpperCase());
        inv.setPurchaseOrderId(req.purchaseOrderId());
        inv.setSupplierId(req.supplierId());
        inv.setInvoiceDate(req.invoiceDate() == null ? java.time.LocalDate.now() : req.invoiceDate());
        inv.setDueDate(req.dueDate());
        inv.setAmount(req.amount());
        inv.setStatus("OPEN");
        return toInvoiceResponse(invoiceRepo.save(inv));
    }

    @Transactional
    public InvoiceResponse markInvoicePaid(String id) {
        InvoiceEntity inv = invoiceRepo.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Invoice", id));
        inv.setStatus("PAID");
        return toInvoiceResponse(invoiceRepo.save(inv));
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> listInvoices() {
        return invoiceRepo.findAll().stream().map(this::toInvoiceResponse).toList();
    }

    // ---- Helpers --------------------------------------------------------

    private PurchaseRequestEntity requireRequest(String id) {
        return requestRepo.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("PurchaseRequest", id));
    }

    private PurchaseRequestResponse toRequestResponse(PurchaseRequestEntity e) {
        return new PurchaseRequestResponse(
                e.getId(), e.getRequestNumber(), e.getTitle(), e.getJustification(),
                e.getProjectId(), e.getRequestedByUserId(), e.getSupplierId(),
                e.getEstimatedCost(), e.getStatus(), e.getApproverUserId(),
                e.getApproverComments(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    private PurchaseOrderResponse toOrderResponse(PurchaseOrderEntity o) {
        return new PurchaseOrderResponse(
                o.getId(), o.getPoNumber(), o.getRequestId(), o.getSupplierId(), o.getIssuedByUserId(),
                o.getIssuedOn(), o.getExpectedDelivery(), o.getTotalAmount(), o.getStatus(), o.getNotes()
        );
    }

    private InvoiceResponse toInvoiceResponse(InvoiceEntity i) {
        return new InvoiceResponse(i.getId(), i.getInvoiceNumber(), i.getPurchaseOrderId(),
                i.getSupplierId(), i.getInvoiceDate(), i.getDueDate(), i.getAmount(), i.getStatus());
    }
}
