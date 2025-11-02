import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ConverterGUI extends JFrame {
    private final JTextField filePathField;

    public ConverterGUI() {
        setTitle("PDF Generator - Excel / PPT / Text -> PDF");
        setSize(600, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JLabel label = new JLabel("Select a file:");
        add(label);

        filePathField = new JTextField(35);
        add(filePathField);

        JButton browseBtn = new JButton("Browse");
        add(browseBtn);

        JButton textBtn = new JButton("Text → PDF");
        JButton excelBtn = new JButton("Excel → PDF");
        JButton pptBtn = new JButton("PPT → PDF");

        add(textBtn);
        add(excelBtn);
        add(pptBtn);

        browseBtn.addActionListener(e -> selectFile());
        textBtn.addActionListener(e -> convertFile("text"));
        excelBtn.addActionListener(e -> convertFile("excel"));
        pptBtn.addActionListener(e -> convertFile("ppt"));

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            filePathField.setText(f.getAbsolutePath());
        }
    }

    private void convertFile(String type) {
        String inputPath = filePathField.getText().trim();
        if (inputPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ Please select a file first!");
            return;
        }

        String outputPath;
        int idx = inputPath.lastIndexOf('.');
        if (idx > 0) {
            outputPath = inputPath.substring(0, idx) + ".pdf";
        } else {
            outputPath = inputPath + ".pdf";
        }

        try {
            if ("text".equals(type)) {
                TextToPDF.convert(inputPath, outputPath);
            } else if ("excel".equals(type)) {
                ExcelToPDF.convert(inputPath, outputPath);
            } else if ("ppt".equals(type)) {
                PPTToPDF.convert(inputPath, outputPath);
            } else {
                throw new IllegalArgumentException("Unknown conversion type: " + type);
            }
            JOptionPane.showMessageDialog(this, "✅ Converted Successfully!\nSaved at: " + outputPath);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Conversion Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ConverterGUI::new);
    }
}
