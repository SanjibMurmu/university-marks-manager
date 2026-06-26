package com.sanju.marks;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

/**
 * Utility service responsible for dynamic document generation.
 * Utilizes OpenPDF to transform raw student grade data into formatted, 
 * downloadable binary PDF report cards.
 *
 * @author Sanjib Murmu
 */

@Service
public class PdfService {

    public byte[] generateReportCard(StudentResultDTO student) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Header
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Jadavpur University", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Information Technology Department\nOfficial Report Card\n\n");
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            // Student Info
            document.add(new Paragraph("Name: " + student.getName()));
            document.add(new Paragraph("Roll Number: " + student.getRollNo() + "\n\n"));

            // Grades
            document.add(new Paragraph("Operating Systems (OOS): " + student.getOos()));
            document.add(new Paragraph("Computer Networks (CN): " + student.getCn()));
            document.add(new Paragraph("Mathematics: " + student.getMaths()));
            
            // Total
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            document.add(new Paragraph("\nTotal Score: " + student.getTotal(), totalFont));

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}