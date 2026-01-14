package com.taskreminder.app.controller;

import com.taskreminder.app.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        passwordResetService.sendResetLink(email);

        redirectAttributes.addFlashAttribute(
                "success",
                "Please check your mail, a reset link has been sent");

        return "redirect:/auth";
    }
    @GetMapping("/reset-password")
    public String resetPasswordPage(
            @RequestParam String token,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!passwordResetService.isTokenValid(token)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Invalid or expired reset link");
            return "redirect:/auth";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Passwords do not match");
            return "redirect:/auth/reset-password?token=" + token;
        }

        passwordResetService.resetPassword(token, password);

        redirectAttributes.addFlashAttribute(
                "success", "Password reset successfully! You may login");

        return "redirect:/auth";
    }

}