package com.taskreminder.app.controller;

import com.taskreminder.app.dto.ProfileUpdateDTO;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.repository.UserRepository;
import com.taskreminder.app.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.util.Collections;
import java.util.UUID;

@Controller
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private UserRepository userRepository;

    private final String UPLOAD_DIR = "uploads/profiles/";

    @GetMapping
    public String showProfile(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute ProfileUpdateDTO dto, Principal principal, Model model) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(dto.getDisplayName());
        user.setMobileNumber(dto.getMobileNumber());

        if (dto.getProfileImage() != null && !dto.getProfileImage().isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" +
                        StringUtils.cleanPath(dto.getProfileImage().getOriginalFilename());

                Path uploadPath = Paths.get(UPLOAD_DIR);

                try {
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                } catch (IOException e) {
                    model.addAttribute("error", "Server Error: Could not create upload directory.");
                    return "profile";
                }

                try (var inputStream = dto.getProfileImage().getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

                    user.setProfilePicturePath("/" + UPLOAD_DIR + fileName);
                } catch (IOException e) {
                    model.addAttribute("error", "File Error: Failed to save the uploaded image.");
                    return "profile";
                }

            } catch (Exception e) {
                model.addAttribute("error", "An unexpected error occurred during the upload.");
                return "profile";
            }
        }
        userRepository.save(user);
        refreshSecurityContext(user);
        return "redirect:/api/profile?success";
    }
    @PostMapping("/remove-picture")
    public String removeProfilePicture(Principal principal, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String currentPath = user.getProfilePicturePath();

        if (currentPath != null && !currentPath.isEmpty()) {
            try {
                String relativePath = currentPath.startsWith("/") ? currentPath.substring(1) : currentPath;
                Path fileToDeletePath = Paths.get(relativePath);


                if (Files.exists(fileToDeletePath)) {
                    Files.delete(fileToDeletePath);
                }

                user.setProfilePicturePath(null);
                userRepository.save(user);

                redirectAttributes.addFlashAttribute("success", "Profile picture removed successfully.");
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to delete the image file from the server.");
            }
        }
        refreshSecurityContext(user);

        return "redirect:/api/profile";
    }
    private void refreshSecurityContext(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
