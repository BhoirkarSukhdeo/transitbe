package com.axisbank.transit.authentication.model.DTO;

import com.axisbank.transit.userDetails.constants.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class UserDetailsAdminDTO {
    private String userId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobile;
    @NotBlank(message = "username should not be blank")
    private String username;
    private String userType;
    private Gender gender;
    @NotBlank(message = "email should not be blank")
    private String email;
    private LocalDate dob;
    private boolean isActive;
    private List<String> roles;

    public UserDetailsAdminDTO() {
    }

    public UserDetailsAdminDTO(String firstName, String middleName, String lastName, String mobile,
                               String username, String userType, Gender gender, String email, LocalDate dob,
                               List<String> roles) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.username = username;
        this.userType = userType;
        this.gender = gender;
        this.email = email;
        this.dob = dob;
        this.roles = roles;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
