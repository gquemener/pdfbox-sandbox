import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String INPUT_FILE_PATH = "./src/main/resources/original.pdf";
    private static final String SOURCE_FILE_PATH_TEMPLATE = "./src/main/resources/page%s.png";
    private static final String OUTPUT_FILE_PATH = "./src/main/resources/result.pdf";

    public static void main(String[] args) {
        Overlay overlayer = new Overlay();
        overlayer.setOverlayPosition(Overlay.Position.FOREGROUND);
        overlayer.setInputFile(INPUT_FILE_PATH);

        Map<Integer, PDDocument> overlays = new HashMap<>();
        for (int pageNumber = 1; pageNumber < 6; pageNumber++) {
            overlays.put(pageNumber, getAnnotationDocument(pageNumber));
        }

        try {
            PDDocument result = overlayer.overlayDocuments(overlays);
            result.save(OUTPUT_FILE_PATH);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                overlayer.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static PDDocument getAnnotationDocument(int pageNumber) {
        PDDocument doc = new PDDocument();
        try {
            PDImageXObject pdImage = PDImageXObject.createFromFile(
                    String.format(SOURCE_FILE_PATH_TEMPLATE, pageNumber),
                    doc
            );
            PDRectangle mediaBox = PDRectangle.LETTER;
            PDPage page = new PDPage(mediaBox);
            doc.addPage(page);
            PDPageContentStream contents = new PDPageContentStream(doc, page);

            contents.drawImage(pdImage, 0, 0, mediaBox.getWidth(), mediaBox.getHeight());
            contents.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }
}
