package com.taskreminder.app.controller;

import com.taskreminder.app.entity.User;
import com.taskreminder.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value={"/auth"})
public class AuthController {

    @Autowired
    private UserService userService;

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
            return "redirect:/auth/verify-otp";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error",ex.getMessage());
            return "redirect:/auth";
        }
    }
    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/auth";
    }

    @GetMapping("/verify-otp")
    public String otpPage(){
        return "verify-otp";
    }
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            RedirectAttributes redirect) {

        if (userService.verifyOtp(email, otp)) {
            redirect.addFlashAttribute("success", "Account verified successfully!");
            return "redirect:/login";
        }

        redirect.addFlashAttribute("error", "Invalid or expired OTP");
        return "redirect:/auth/verify-otp";
    }
}
