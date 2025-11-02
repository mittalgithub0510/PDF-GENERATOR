import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;

import java.io.File;

public class PPTToPDF {
    public static void convert(String inputPath, String outputPath) throws Exception {
        File f = new File(inputPath);
        try (POITextExtractor extractor = ExtractorFactory.createExtractor(f);
             PDDocument doc = new PDDocument()) {

            String allText = extractor.getText();
            if (allText == null) allText = "";

            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);

            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;
            float leading = 14;

            String[] lines = allText.split("\\r?\\n");
            for (String line : lines) {
                // Sanitize line - remove characters that can't be encoded in WinAnsiEncoding
                line = sanitizeText(line);
                
                if (y <= margin) {
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    y = page.getMediaBox().getHeight() - margin;
                }

                content.beginText();
                content.newLineAtOffset(margin, y);
                // wrap long line
                for (String part : TextToPDF.splitLineToFitStatic(line, 100)) {
                    content.showText(part);
                    content.endText();
                    y -= leading;
                    if (y <= margin) break;
                    content.beginText();
                    content.newLineAtOffset(margin, y);
                }
                try {
                    content.endText();
                } catch (Exception ignored) {}
                y -= leading;
            }

            content.close();
            doc.save(outputPath);
        }
    }
    
    /**
     * Sanitize text to remove characters that can't be encoded in WinAnsiEncoding.
     * This includes control characters like tabs, and characters outside the WinAnsi range.
     */
    private static String sanitizeText(String text) {
        if (text == null) return "";
        
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            // Replace tabs with spaces
            if (c == '\t') {
                sb.append("    "); // 4 spaces
            }
            // Keep only printable ASCII and common extended ASCII (WinAnsi range)
            else if (c >= 32 && c <= 126) {
                // Standard ASCII printable characters
                sb.append(c);
            }
            else if (c >= 160 && c <= 255) {
                // Extended ASCII (WinAnsi encoding range)
                sb.append(c);
            }
            // Skip other control characters and unsupported characters
        }
        return sb.toString();
    }
}
