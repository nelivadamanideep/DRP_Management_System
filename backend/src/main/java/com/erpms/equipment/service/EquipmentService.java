package com.erpms.equipment.service;

import com.erpms.common.exception.BusinessRuleException;
import com.erpms.common.exception.ResourceNotFoundException;
import com.erpms.common.security.SecurityUtils;
import com.erpms.equipment.dto.*;
import com.erpms.equipment.entity.EquipmentBookingEntity;
import com.erpms.equipment.entity.EquipmentEntity;
import com.erpms.equipment.entity.EquipmentMaintenanceEntity;
import com.erpms.equipment.repository.EquipmentBookingRepository;
import com.erpms.equipment.repository.EquipmentMaintenanceRepository;
import com.erpms.equipment.repository.EquipmentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentBookingRepository bookingRepository;
    private final EquipmentMaintenanceRepository maintenanceRepository;

    public EquipmentService(EquipmentRepository e, EquipmentBookingRepository b, EquipmentMaintenanceRepository m) {
        this.equipmentRepository = e;
        this.bookingRepository = b;
        this.maintenanceRepository = m;
    }

    // ---- Equipment CRUD -------------------------------------------------

    @Transactional
    public EquipmentResponse create(EquipmentRequest req) {
        if (equipmentRepository.existsByAssetTagIgnoreCase(req.assetTag())) {
            throw new BusinessRuleException("Asset tag already exists");
        }
        EquipmentEntity e = new EquipmentEntity();
        applyRequest(e, req);
        e.setAssetTag(req.assetTag().trim().toUpperCase());
        e.setQrCodePayload("ERPMS-EQP:" + e.getAssetTag());
        return toResponse(equipmentRepository.save(e));
    }

    @Transactional
    public EquipmentResponse update(String id, EquipmentRequest req) {
        EquipmentEntity e = equipmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Equipment", id));
        applyRequest(e, req);
        return toResponse(equipmentRepository.save(e));
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> findAll() {
        return equipmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EquipmentResponse findById(String id) {
        return toResponse(equipmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Equipment", id)));
    }

    @Transactional
    public void delete(String id) {
        equipmentRepository.deleteById(id);
    }

    // ---- Bookings -------------------------------------------------------

    @Transactional
    public BookingResponse book(BookingRequest req) {
        if (!req.endTime().isAfter(req.startTime())) {
            throw new BusinessRuleException("Booking end time must be after start time");
        }
        equipmentRepository.findById(req.equipmentId())
                .orElseThrow(() -> ResourceNotFoundException.of("Equipment", req.equipmentId()));
        List<EquipmentBookingEntity> overlaps =
                bookingRepository.findOverlapping(req.equipmentId(), req.startTime(), req.endTime());
        if (!overlaps.isEmpty()) {
            throw new BusinessRuleException("Equipment is already booked in the requested window");
        }
        EquipmentBookingEntity b = new EquipmentBookingEntity();
        b.setEquipmentId(req.equipmentId());
        b.setBookedByUserId(SecurityUtils.currentUserIdOrNull());
        b.setProjectId(req.projectId());
        b.setStartTime(req.startTime());
        b.setEndTime(req.endTime());
        b.setPurpose(req.purpose());
        b.setStatus("SCHEDULED");
        return toBookingResponse(bookingRepository.save(b));
    }

    @Transactional
    public BookingResponse cancelBooking(String id) {
        EquipmentBookingEntity b = bookingRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Booking", id));
        b.setStatus("CANCELLED");
        return toBookingResponse(bookingRepository.save(b));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listBookings(String equipmentId) {
        return bookingRepository.findByEquipmentIdOrderByStartTimeDesc(equipmentId)
                .stream().map(this::toBookingResponse).toList();
    }

    // ---- Maintenance / Calibration -------------------------------------

    @Transactional
    public MaintenanceResponse logMaintenance(MaintenanceRequest req) {
        EquipmentEntity eq = equipmentRepository.findById(req.equipmentId())
                .orElseThrow(() -> ResourceNotFoundException.of("Equipment", req.equipmentId()));
        EquipmentMaintenanceEntity m = new EquipmentMaintenanceEntity();
        m.setEquipmentId(req.equipmentId());
        m.setPerformedByUserId(req.performedByUserId());
        m.setPerformedOn(req.performedOn());
        m.setActivity(req.activity().trim().toUpperCase());
        m.setNotes(req.notes());
        m.setNextDueOn(req.nextDueOn());

        // Roll the equipment's own next-calibration date forward when applicable.
        if ("CALIBRATION".equals(m.getActivity()) && req.nextDueOn() != null) {
            eq.setNextCalibrationDate(req.nextDueOn());
            equipmentRepository.save(eq);
        }
        return toMaintenanceResponse(maintenanceRepository.save(m));
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> listMaintenance(String equipmentId) {
        return maintenanceRepository.findByEquipmentIdOrderByPerformedOnDesc(equipmentId)
                .stream().map(this::toMaintenanceResponse).toList();
    }

    // ---- Mapping --------------------------------------------------------

    private void applyRequest(EquipmentEntity e, EquipmentRequest req) {
        e.setName(req.name().trim());
        e.setDescription(req.description());
        e.setManufacturer(req.manufacturer());
        e.setModelNumber(req.modelNumber());
        e.setSerialNumber(req.serialNumber());
        e.setDepartmentId(req.departmentId());
        e.setLaboratoryLocation(req.laboratoryLocation());
        e.setPurchaseDate(req.purchaseDate());
        e.setNextCalibrationDate(req.nextCalibrationDate());
        if (req.status() != null && !req.status().isBlank()) {
            e.setStatus(req.status().trim().toUpperCase());
        }
    }

    private EquipmentResponse toResponse(EquipmentEntity e) {
        return new EquipmentResponse(e.getId(), e.getAssetTag(), e.getName(), e.getDescription(),
                e.getManufacturer(), e.getModelNumber(), e.getSerialNumber(),
                e.getDepartmentId(), e.getLaboratoryLocation(), e.getPurchaseDate(),
                e.getNextCalibrationDate(), e.getQrCodePayload(), e.getStatus());
    }

    private BookingResponse toBookingResponse(EquipmentBookingEntity b) {
        return new BookingResponse(b.getId(), b.getEquipmentId(), b.getBookedByUserId(), b.getProjectId(),
                b.getStartTime(), b.getEndTime(), b.getStatus(), b.getPurpose(), b.getCreatedAt());
    }

    private MaintenanceResponse toMaintenanceResponse(EquipmentMaintenanceEntity m) {
        return new MaintenanceResponse(m.getId(), m.getEquipmentId(), m.getPerformedByUserId(),
                m.getPerformedOn(), m.getActivity(), m.getNotes(), m.getNextDueOn());
    }
}
