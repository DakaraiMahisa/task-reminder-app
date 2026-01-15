package com.taskreminder.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Setter
@Table(name = "user")
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean verified = false;

    private String otp;

    @Column(nullable = false)
    private Boolean enabled =false;

    private String mobileNumber;
    private String profilePicturePath;
    private LocalDateTime otpExpiry;
    private LocalDateTime createdAt = LocalDateTime.now();

    public User(){}

    public User(String name,String email, String password,boolean verified,String otp,boolean enabled,LocalDateTime otpExpiry,LocalDateTime createdAt){
        this.name = name;
        this.email = email;
        this.password = password;
        this.verified = verified;
        this.otp = otp;
        this.enabled = enabled;
        this.otpExpiry =otpExpiry;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getOtp() {
        return otp;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

}
