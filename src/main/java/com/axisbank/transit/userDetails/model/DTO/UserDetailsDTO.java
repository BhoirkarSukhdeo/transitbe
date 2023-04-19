package com.axisbank.transit.userDetails.model.DTO;

import com.axisbank.transit.userDetails.constants.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO {
    private String userId;
    private String name;
    private String mobile;
    private String username;
    private Gender gender;
    private String email;
    private LocalDate dob;
    private String occupation;
    private Boolean isCardLinked;
    private LocalDateTime lastlogin;
    private String userType;
    private boolean isActive;
    private List<String> roles;
}
