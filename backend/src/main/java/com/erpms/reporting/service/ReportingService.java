package com.erpms.reporting.service;

import com.erpms.common.exception.BusinessRuleException;
import com.erpms.project.entity.Project;
import com.erpms.project.repository.ProjectRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/**
 * Generates operational reports in PDF (OpenPDF) and Excel (Apache POI) formats.
 *
 * <p>Kept intentionally lean: each report knows how to render a homogeneous
 * dataset. Add new methods here rather than sprinkling report logic across
 * domain services.
 */
@Service
public class ReportingService {

    private final ProjectRepository projectRepository;

    public ReportingService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // ---- Projects PDF ---------------------------------------------------

    public byte[] projectsPdf() {
        List<Project> projects = projectRepository.findAll();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);
            doc.add(new Paragraph("ERPMS — Projects Report", titleFont));
            doc.add(new Paragraph("Generated " + LocalDateTime.now()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), metaFont));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[]{2, 4, 2, 2, 2, 3});
            table.setWidthPercentage(100);
            addHeader(table, "Code", "Title", "Priority", "Risk", "Status", "Budget");
            for (Project p : projects) {
                table.addCell(p.getProjectCode());
                table.addCell(p.getTitle());
                table.addCell(p.getPriority());
                table.addCell(p.getRiskLevel());
                table.addCell(p.getStatus());
                table.addCell(String.valueOf(p.getApprovedBudget()));
            }
            doc.add(table);
            doc.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new BusinessRuleException("Failed to build PDF: " + ex.getMessage());
        }
    }

    // ---- Projects Excel -------------------------------------------------

    public byte[] projectsExcel() {
        List<Project> projects = projectRepository.findAll();
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Projects");
            org.apache.poi.ss.usermodel.Font boldFont = wb.createFont();
            boldFont.setBold(true);
            CellStyle head = wb.createCellStyle();
            head.setFont(boldFont);

            String[] headers = {"Code", "Title", "Priority", "Risk", "Status", "Budget",
                    "Planned Start", "Planned End"};
            Row hr = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = hr.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(head);
            }

            int r = 1;
            for (Project p : projects) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(nullSafe(p.getProjectCode()));
                row.createCell(1).setCellValue(nullSafe(p.getTitle()));
                row.createCell(2).setCellValue(nullSafe(p.getPriority()));
                row.createCell(3).setCellValue(nullSafe(p.getRiskLevel()));
                row.createCell(4).setCellValue(nullSafe(p.getStatus()));
                row.createCell(5).setCellValue(p.getApprovedBudget() == null ? 0 : p.getApprovedBudget().doubleValue());
                row.createCell(6).setCellValue(p.getPlannedStartDate() == null ? "" : p.getPlannedStartDate().toString());
                row.createCell(7).setCellValue(p.getPlannedEndDate() == null ? "" : p.getPlannedEndDate().toString());
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new BusinessRuleException("Failed to build Excel: " + ex.getMessage());
        }
    }

    // ---- Helpers --------------------------------------------------------

    private void addHeader(PdfPTable table, String... cells) {
        for (String c : cells) {
            PdfPCell cell = new PdfPCell(new Paragraph(c,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private String nullSafe(String v) { return v == null ? "" : v; }
}
