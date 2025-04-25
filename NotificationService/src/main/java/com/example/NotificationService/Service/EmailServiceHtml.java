package com.example.NotificationService.Service;
import com.example.NotificationService.Config.ThymeleafConfig;
import com.example.NotificationService.Service.Kafka.NotificationProducer;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.Context;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceHtml {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ThymeleafConfig templateEngine;
    @Autowired
    private NotificationProducer notificationProducer;

    @Autowired
    private TemplateEngine emailTemplateEngine;

    public void sendHtmlEmail(String to, String subject, String name, String event, String date, String seat) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        // Load Thymeleaf email template
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("event", event);
        context.setVariable("date", date);
        context.setVariable("seat", seat);
        String htmlContent = emailTemplateEngine.process("email_template", (IContext) context);

        // Set email properties
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
    public void sendEmailWithAttachments(String to, String name, String event, String date, String seat) throws MessagingException, IOException, WriterException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        // Generate PDF Ticket
        byte[] pdfBytes = generatePDFTicket(name, event, date, seat);

        // Generate QR Code
        byte[] qrCodeBytes = generateQRCode(event + "-" + seat);

        // Load Thymeleaf HTML template
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("event", event);
        context.setVariable("date", date);
        context.setVariable("seat", seat);
        String htmlContent = emailTemplateEngine.process("email_template", context);

        // Set email properties
        helper.setTo(to);
        helper.setSubject("Your Booking Confirmation");
        helper.setText(htmlContent, true);

        // Attach PDF Ticket
        helper.addAttachment("Ticket.pdf", new ByteArrayResource(pdfBytes));

        // Attach QR Code
        helper.addAttachment("QRCode.png", new ByteArrayResource(qrCodeBytes));

        // Send email
        mailSender.send(message);
        //notificationProducer.sendNotification();


    }

    // PDF Generation
    private byte[] generatePDFTicket(String name, String event, String date, String seat) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Booking Confirmation");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Name: " + name);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Event: " + event);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Date: " + date);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Seat: " + seat);
        contentStream.endText();
        contentStream.close();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();
        return outputStream.toByteArray();
    }

    // QR Code Generation
    private byte[] generateQRCode(String data) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", outputStream);
        return outputStream.toByteArray();
    }
}
