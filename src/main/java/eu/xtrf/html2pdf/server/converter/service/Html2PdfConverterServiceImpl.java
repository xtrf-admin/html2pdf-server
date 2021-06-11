package eu.xtrf.html2pdf.server.converter.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class Html2PdfConverterServiceImpl implements Html2PdfConverterService {
    @Override
    public File generatePdfToFile(String content, String header, String footer, String styles, String resourcesPath) {
        return null;
    }

    @Override
    public File generatePdfToFile(String themeContent, String documentContent, String styles, String resourcesPath) throws IOException {
        File tempPdfFile = File.createTempFile("generated_", ".pdf");

        String pageWithTheme = themeContent.replace("#DOCUMENT_CONTENT", documentContent);

        try {
            String xhtml = htmlToXhtml(pageWithTheme);
            xhtmlToPdf(xhtml, tempPdfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempPdfFile;
    }

    private static String htmlToXhtml(String inputHTML) throws IOException {
        Document document = Jsoup.parse(inputHTML, "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }

    private static void xhtmlToPdf(String xhtml, File outputPdf) throws IOException {
        ITextRenderer renderer = new ITextRenderer();
        SharedContext sharedContext = renderer.getSharedContext();
        sharedContext.setPrint(true);
        sharedContext.setInteractive(false);
//        sharedContext.setReplacedElementFactory(new ImageReplacedElementFactory());
        sharedContext.getTextRenderer().setSmoothingThreshold(0);
        renderer.getFontResolver().addFontDirectory("/usr/share/fonts/", true);
        renderer.setDocumentFromString(xhtml, ""); // TODO czy w ogóle jest sens używać baseUrl?
        renderer.layout();
        OutputStream outputStream = new FileOutputStream(outputPdf);
        renderer.createPDF(outputStream);
        // put this in finally
        outputStream.close();
    }

}
