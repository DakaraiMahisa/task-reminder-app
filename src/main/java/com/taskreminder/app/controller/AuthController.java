package com.taskreminder.app.controller;

import com.taskreminder.app.entity.User;
import com.taskreminder.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;


    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/resend-otp")
    public ResponseEntity<Void> resendOtp(@RequestParam String email) {
        userService.resendOtp(email);
        return ResponseEntity.ok().build();
    }
    @GetMapping
    public String authPage(Model model){
       model.addAttribute("user",new User());
       return "auth";
    }
    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes redirect){
        try{
            userService.register(user);
            redirect.addAttribute("success","Account created! Please verify");
            redirect.addAttribute("email", user.getEmail());
            return "redirect:/auth/verify-otp";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error",ex.getMessage());
            return "redirect:/auth";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "redirect:/auth";
    }

    @GetMapping("/verify-otp")
    public String otpPage(@RequestParam String email, Model model){
        model.addAttribute("email", email);
        return "verify-otp";
    }
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            RedirectAttributes redirect) {

        try {
            userService.verifyOtp(email, otp);

            redirect.addFlashAttribute("success",
                    "Account verified successfully!");
            return "redirect:/auth";

        } catch (RuntimeException ex) {

            redirect.addFlashAttribute("error", ex.getMessage());
            redirect.addAttribute("email", email);
            return "redirect:/auth/verify-otp";
        }
    }

}
