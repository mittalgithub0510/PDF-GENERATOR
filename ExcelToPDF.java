import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;

public class ExcelToPDF {
    public static void convert(String inputPath, String outputPath) throws Exception {
        try (FileInputStream fis = new FileInputStream(inputPath);
             Workbook workbook = WorkbookFactory.create(fis);
             PDDocument doc = new PDDocument()) {

            Sheet sheet = workbook.getSheetAt(0);

            PDPage page = new PDPage();
            doc.addPage(page);

            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.setFont(PDType1Font.HELVETICA, 10);

            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;
            float leading = 12;

            for (Row row : sheet) {
                StringBuilder sb = new StringBuilder();
                for (Cell cell : row) {
                    String cellText = getCellText(cell);
                    sb.append(cellText).append("  |  ");
                }
                String rowText = sb.toString();
                if (y <= margin) {
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    content.setFont(PDType1Font.HELVETICA, 10);
                    y = page.getMediaBox().getHeight() - margin;
                }

                content.beginText();
                content.newLineAtOffset(margin, y);
                // wrap row if long
                for (String part : TextToPDF.splitLineToFitStatic(rowText, 120)) {
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

    private static String getCellText(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        return "";
                    }
                }
            case BLANK:
            default:
                return "";
        }
    }
}
