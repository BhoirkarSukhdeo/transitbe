package com.axisbank.transit.userDetails.model.DAO;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.BaseEntity;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DTO.UserConfigurationDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDate;

@Audited
@Entity(name = "user_detail")
public class DAOUser extends BaseEntity {

	@NotAudited
	@Column(name = "user_id")
	private String userId;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name")
	private String lastName;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender")
	private Gender gender;

	@JsonFormat(pattern="dd/MM/yyyy")
	@Column(name = "dob")
	private LocalDate dob;

	@Column(name = "occupation")
	private String occupation;

	@Column(name = "pg_customer_id")
	private String pgCustomerId;

	@Lob
	@Column(name = "user_configuration")
	private String userConfiguration;

	@OneToOne
	@JoinColumn(name = "authentication_id")
	private AuthenticationDAO authenticationDAO;

	public AuthenticationDAO getAuthenticationDAO() {
		return authenticationDAO;
	}

	public void setAuthenticationDAO(AuthenticationDAO authenticationDAO) {
		this.authenticationDAO = authenticationDAO;
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

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getPgCustomerId() {
		return pgCustomerId;
	}

	public void setPgCustomerId(String pgCustomerId) {
		this.pgCustomerId = pgCustomerId;
	}

	public UserConfigurationDTO getUserConfiguration() throws JsonProcessingException {
		if(userConfiguration == null){
			return null;
		}
		return CommonUtils.convertJsonStringToObject(userConfiguration, UserConfigurationDTO.class);
	}

	public void setUserConfiguration(UserConfigurationDTO userConfiguration) throws JsonProcessingException {
		if(userConfiguration == null){
			this.userConfiguration=null;
		}
		else {
			this.userConfiguration = CommonUtils.convertObjectToJsonString(userConfiguration);
		}
	}
}
