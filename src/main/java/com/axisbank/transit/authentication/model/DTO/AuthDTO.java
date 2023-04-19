package com.axisbank.transit.authentication.model.DTO;

import com.axisbank.transit.authentication.constants.RegistrationType;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class AuthDTO {
	private String mobile;
	private String name;
	private String lastName="";
	private String lastFourDigitCardNumber;
	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDate dob;
	private String cifId;
	private String emailId;
	private String panNumber;

	@NotNull(message = "registrationType is mandatory")
	private RegistrationType registrationType;

	public RegistrationType getRegistrationType() {
		return registrationType;
	}

	public void setRegistrationType(RegistrationType registrationType) {
		this.registrationType = registrationType;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastFourDigitCardNumber() {
		return lastFourDigitCardNumber;
	}

	public void setLastFourDigitCardNumber(String lastFourDigitCardNumber) {
		this.lastFourDigitCardNumber = lastFourDigitCardNumber;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getCifId() {
		return cifId;
	}

	public void setCifId(String cifId) {
		this.cifId = cifId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}
}
