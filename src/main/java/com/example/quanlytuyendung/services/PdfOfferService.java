package com.example.quanlytuyendung.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfOfferService {

    private final Path offerDir = Paths.get("uploads", "offers");

    public PdfOfferService() {
        try {
            Files.createDirectories(offerDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String cleanText(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "")
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D")
                .replaceAll("[^\\x00-\\x7F]", "");
    }

    public byte[] generateOfferLetterBytes(String candidateName, String jobTitle, String department, String baseSalary, String bonus, String startDate) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            String safeCandidate = cleanText(candidateName);
            String safeJob = cleanText(jobTitle);
            String safeDept = cleanText(department);
            String safeSalary = cleanText(baseSalary);
            String safeBonus = cleanText(bonus != null && !bonus.isEmpty() ? bonus : "Standard Policy");
            String safeDate = cleanText(startDate != null && !startDate.isEmpty() ? startDate : "As agreed");

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                // Header Bar
                cs.setNonStrokingColor(37 / 255f, 99 / 255f, 235 / 255f); // Blue #2563EB
                cs.addRect(0, 800, 595, 42);
                cs.fill();

                // Header Title
                cs.setNonStrokingColor(1f, 1f, 1f);
                cs.beginText();
                cs.setFont(titleFont, 16);
                cs.newLineAtOffset(50, 812);
                cs.showText("TALENTFLOW ENTERPRISE ATS - JOB OFFER LETTER");
                cs.endText();

                // Document Meta
                cs.setNonStrokingColor(100 / 255f, 116 / 255f, 139 / 255f);
                cs.beginText();
                cs.setFont(regularFont, 10);
                cs.newLineAtOffset(50, 770);
                cs.showText("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                cs.endText();

                // Salutation
                cs.setNonStrokingColor(15 / 255f, 23 / 255f, 42 / 255f);
                cs.beginText();
                cs.setFont(boldFont, 14);
                cs.newLineAtOffset(50, 730);
                cs.showText("Dear " + safeCandidate + ",");
                cs.endText();

                // Body Paragraph 1
                cs.beginText();
                cs.setFont(regularFont, 11);
                cs.newLineAtOffset(50, 700);
                cs.showText("We are delighted to extend an official offer of employment for the position of:");
                cs.endText();

                // Job Title Highlight
                cs.beginText();
                cs.setFont(boldFont, 14);
                cs.newLineAtOffset(70, 675);
                cs.showText(safeJob + " (" + safeDept + ")");
                cs.endText();

                // Details Box
                cs.setNonStrokingColor(241 / 255f, 245 / 255f, 249 / 255f); // Slate-100
                cs.addRect(50, 480, 495, 170);
                cs.fill();

                cs.setStrokingColor(203 / 255f, 213 / 255f, 225 / 255f); // Slate-300
                cs.addRect(50, 480, 495, 170);
                cs.stroke();

                // Items inside box
                cs.setNonStrokingColor(15 / 255f, 23 / 255f, 42 / 255f);
                int y = 620;

                String[][] items = {
                    {"Position / Title:", safeJob},
                    {"Department:", safeDept},
                    {"Base Monthly Salary:", safeSalary},
                    {"Sign-on Bonus / Allowance:", safeBonus},
                    {"Official Start Date:", safeDate},
                    {"Work Location:", "Headquarters / Hybrid Flexible"}
                };

                for (String[] item : items) {
                    cs.beginText();
                    cs.setFont(boldFont, 11);
                    cs.newLineAtOffset(70, y);
                    cs.showText(item[0]);
                    cs.endText();

                    cs.beginText();
                    cs.setFont(regularFont, 11);
                    cs.newLineAtOffset(260, y);
                    cs.showText(item[1]);
                    cs.endText();

                    y -= 24;
                }

                // Terms
                cs.beginText();
                cs.setFont(regularFont, 10);
                cs.newLineAtOffset(50, 440);
                cs.showText("This offer is contingent upon successful verification of references and credentials.");
                cs.endText();

                cs.beginText();
                cs.setFont(regularFont, 10);
                cs.newLineAtOffset(50, 420);
                cs.showText("Please confirm your acceptance by signing and returning this letter within 5 business days.");
                cs.endText();

                // Signature section
                cs.beginText();
                cs.setFont(boldFont, 11);
                cs.newLineAtOffset(50, 340);
                cs.showText("For TalentFlow Recruitment:");
                cs.endText();

                cs.beginText();
                cs.setFont(regularFont, 11);
                cs.newLineAtOffset(50, 320);
                cs.showText("HR Director & Management Board");
                cs.endText();

                cs.beginText();
                cs.setFont(regularFont, 10);
                cs.newLineAtOffset(50, 300);
                cs.showText("[Digitally Verified & Approved via ATS System]");
                cs.endText();

                cs.beginText();
                cs.setFont(boldFont, 11);
                cs.newLineAtOffset(350, 340);
                cs.showText("Candidate Acceptance:");
                cs.endText();

                cs.beginText();
                cs.setFont(regularFont, 11);
                cs.newLineAtOffset(350, 320);
                cs.showText("Signature: ______________________");
                cs.endText();

                cs.beginText();
                cs.setFont(regularFont, 10);
                cs.newLineAtOffset(350, 300);
                cs.showText("Date: __________________________");
                cs.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    public String saveOfferPdfToFile(Long offerId, String candidateName, String jobTitle, String department, String baseSalary, String bonus, String startDate) throws IOException {
        byte[] pdfBytes = generateOfferLetterBytes(candidateName, jobTitle, department, baseSalary, bonus, startDate);
        String safeName = cleanText(candidateName).replaceAll("[^a-zA-Z0-9]", "_");
        String filename = "Offer_" + offerId + "_" + safeName + ".pdf";
        Path targetPath = this.offerDir.resolve(filename);

        try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
            fos.write(pdfBytes);
        }

        return "/uploads/offers/" + filename;
    }
}
