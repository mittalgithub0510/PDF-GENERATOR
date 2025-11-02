import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextToPDF {

    public static void convert(String inputPath, String outputPath) throws IOException {
        try (PDDocument doc = new PDDocument();
             BufferedReader reader = new BufferedReader(new FileReader(inputPath))) {

            PDPage page = new PDPage();
            doc.addPage(page);

            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.setFont(PDType1Font.HELVETICA, 12);

            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;
            float leading = 14;

            String line;
            while ((line = reader.readLine()) != null) {
                if (y <= margin) {
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    content.setFont(PDType1Font.HELVETICA, 12);
                    y = page.getMediaBox().getHeight() - margin;
                }

                content.beginText();
                content.newLineAtOffset(margin, y);

                // Wrap long lines
                for (String part : splitLineToFit(line, 80)) {
                    content.showText(part);
                    content.endText();
                    y -= leading;
                    if (y <= margin) break;
                    content.beginText();
                    content.newLineAtOffset(margin, y);
                }

                try {
                    content.endText();
                } catch (IOException ignored) {}
                y -= leading;
            }

            content.close();
            doc.save(outputPath);
        }
    }

    // Helper method to split long text into multiple lines
    private static String[] splitLineToFit(String line, int approxCharsPerLine) {
        if (line == null) return new String[]{""};
        if (line.length() <= approxCharsPerLine) return new String[]{line};

        List<String> parts = new ArrayList<>();
        int start = 0;

        while (start < line.length()) {
            int end = Math.min(start + approxCharsPerLine, line.length());
            if (end < line.length()) {
                int lastSpace = line.lastIndexOf(' ', end);
                if (lastSpace > start) {
                    end = lastSpace;
                }
            }
            parts.add(line.substring(start, end).trim());
            start = end;
            while (start < line.length() && line.charAt(start) == ' ') {
                start++;
            }
        }
        return parts.toArray(new String[0]);
    }

    // ✅ Public static version (for ExcelToPDF & PPTToPDF reuse)
    public static String[] splitLineToFitStatic(String line, int approxCharsPerLine) {
        return splitLineToFit(line, approxCharsPerLine);
    }
}
