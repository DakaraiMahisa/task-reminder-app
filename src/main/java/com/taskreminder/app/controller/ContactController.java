package com.taskreminder.app.controller;

import com.taskreminder.app.entity.ContactMessage;
import com.taskreminder.app.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contact")
    public String showContactPage(Model model) {
        model.addAttribute("contactMessage", new ContactMessage());
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContactForm(
            @Valid @ModelAttribute("contactMessage") ContactMessage contactMessage,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model
    ) {


        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage",
                    "Please correct the highlighted fields and try again.");
            return "contact";
        }

        // 2. Simple bot detection (honeypot)
        if (request.getParameter("company") != null &&
                !request.getParameter("company").isBlank()) {

            model.addAttribute("errorMessage",
                    "Invalid submission detected.");
            return "contact";
        }

        // 3. Rate limiting (session-based)
        Long lastSubmission =
                (Long) request.getSession().getAttribute("lastContactSubmission");

        long now = System.currentTimeMillis();

        if (lastSubmission != null && (now - lastSubmission) < 30_000) {
            model.addAttribute("errorMessage",
                    "Please wait before submitting another message.");
            return "contact";
        }

        request.getSession().setAttribute("lastContactSubmission", now);

        // 4. Persist + notify
        contactService.handleContactMessage(contactMessage);

        model.addAttribute("successMessage",
                "Thank you for contacting us. Your message has been received.");

        return "contact";
    }
}
