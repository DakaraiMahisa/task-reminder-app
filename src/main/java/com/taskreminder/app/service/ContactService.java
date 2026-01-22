package com.taskreminder.app.service;

import com.taskreminder.app.entity.ContactMessage;
import com.taskreminder.app.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class ContactService {

    private final EmailService emailService;
    private final ContactMessageRepository repository;

    @Value("${spring.mail.username}")
    private String fromEmail;


    public ContactService(ContactMessageRepository repository,EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    public void handleContactMessage(ContactMessage message) {
        message.setMessage(
                HtmlUtils.htmlEscape(message.getMessage())
        );
        repository.save(message);

        // 3. Sending email to admin
        sendAdminNotification(message);
        //  4.Sending auto reply
        sendAutoReply(message);
    }
    private void sendAdminNotification(ContactMessage message) {

        String body = """
            <h3>New Contact Message</h3>
            <p><strong>Name:</strong> %s</p>
            <p><strong>Email:</strong> %s</p>
            <p><strong>Subject:</strong> %s</p>
            <hr>
            <p>%s</p>
            """.formatted(
                message.getName(),
                message.getEmail(),
                message.getSubject(),
                message.getMessage()
        );

        emailService.sendEmail(
                fromEmail,
                "New Contact Message - " + message.getSubject(),
                body
        );
    }
    private void sendAutoReply(ContactMessage message) {

        String body = """
            <p>Hello %s,</p>

            <p>Thank you for contacting the <strong>Task Reminder App</strong>.</p>

            <p>
            We have received your message and it has been recorded successfully.
            Our team will review it and get back to you if a response is required.
            </p>

            <p>
            Please note that this application is currently under development,
            so response times may vary.
            </p>

            <p>
            Best regards,<br>
            Task Reminder App Team
            </p>
            """.formatted(message.getName());

        emailService.sendEmail(
                message.getEmail(),
                "We’ve received your message",
                body
        );
    }
}
