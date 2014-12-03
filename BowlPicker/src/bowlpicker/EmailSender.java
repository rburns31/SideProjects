package bowlpicker;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    private final String recipientAddress;
    private final String senderAddress;

    public EmailSender(String recipientAddress, String senderAddress) {
        this.recipientAddress = recipientAddress;
        this.senderAddress = senderAddress;
    }

    private void sender() {
        String host = "localhost";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderAddress));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddress));
            message.setSubject("This is the subject line");
            message.setText("This is the actual message");
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            System.out.println(mex.getMessage());
        }
    }
}