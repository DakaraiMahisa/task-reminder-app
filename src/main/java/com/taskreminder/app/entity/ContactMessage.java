package com.taskreminder.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Data
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String subject;

    @NotBlank
    @Column(length = 2000)
    private String message;

    private LocalDateTime submittedAt=LocalDateTime.now();

    public ContactMessage() {
        this.submittedAt = LocalDateTime.now();
    }
}
