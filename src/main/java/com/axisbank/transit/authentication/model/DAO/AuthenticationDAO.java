package com.axisbank.transit.authentication.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;
import com.axisbank.transit.core.model.DAO.FeedbackDAO;
import com.axisbank.transit.core.model.DAO.NotificationDAO;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.journey.model.DAO.FavouriteAddressDAO;
import com.axisbank.transit.journey.model.DAO.JourneyPlannerRouteDAO;
import com.axisbank.transit.kmrl.model.DAO.TicketDAO;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DAO.CardLimitDetailsDAO;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.model.DAO.DeviceInfo;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.*;

@Entity(name = "authentication")
@Audited
public class AuthenticationDAO extends BaseEntity {

	@Column(name = "username", unique=true)
	private String userName;

	@Column(name = "password")
	private String password;

	@Size(min = 10, max = 14)
	@Column(name = "mobile", unique=true)
	private String mobile;

	@Column(name = "otp_verification")
	private Boolean otpVerification = false;

	@Column(name = "email")
	private String email;

	@NotAudited
	@Column(name = "last_login")
	private LocalDateTime lastlogin;

	@Column(name = "user_type")
	private String userType = "transit_user";

	@Size(min = 6)
	@Column(name = "mpin")
	private String mpin;

	@OneToOne(mappedBy = "authenticationDAO", cascade = CascadeType.ALL)
	private DAOUser daoUser;

	@OneToOne(mappedBy = "authenticationDAO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private CardDetailsDAO cardDetailsDAO;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "authentication_role",
			joinColumns = @JoinColumn(name = "authentication_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	@AuditJoinTable(inverseJoinColumns = { @JoinColumn(name = "role_id")})
	private Collection<Role> roles = new HashSet<>();

	@OneToOne(mappedBy = "authenticationDAO",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private DeviceInfo deviceInfo;

	@NotAudited
	@OneToMany(mappedBy = "authenticationDAO", cascade = CascadeType.ALL)
	private Set<FeedbackDAO> feedbackDAOSet;

	@NotAudited
	@OneToMany(mappedBy = "authenticationDAO", cascade = CascadeType.ALL)
	private Set<FavouriteAddressDAO> favouriteAddressDAOSet;

	@NotAudited
	@OneToMany(mappedBy = "authenticationDAO",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<TransactionDAO> transactionDAOS;

	@NotAudited
	@OneToMany(mappedBy = "authenticationDAO", cascade = CascadeType.ALL)
	private Set<JourneyPlannerRouteDAO> journeyPlannerRouteDAOSet;

	@NotAudited
	@OneToMany(mappedBy = "authenticationDAO", cascade = CascadeType.ALL)
	private Set<TicketDAO> ticketDAOSet;

	@OneToMany(mappedBy = "authenticationDAO", cascade = CascadeType.ALL)
	private Set<ExploreDAO> exploreDAOS;

	@NotAudited
	@OneToOne(mappedBy = "authenticationDAO", cascade = CascadeType.ALL)
	private SessionDAO sessionDAO;

	@NotAudited
	@OneToMany(mappedBy = "authenticationDAO", cascade = CascadeType.ALL)
	private Set<MpinLog> mpinLogs;

	@NotAudited
	@OneToMany(mappedBy = "authenticationDAO", cascade = CascadeType.ALL)
	private Set<NotificationDAO> notificationDAOS;

	@ManyToMany(targetEntity = ExploreDAO.class, mappedBy = "authenticationDAOSet")
	private Set<ExploreDAO> exploreDAOSet = new HashSet<>();

	@NotAudited
	@OneToMany(mappedBy = "authenticationDAO", cascade = CascadeType.ALL)
	private Set<LoginLog> loginLogs;

	@NotAudited
	@OneToOne(mappedBy = "authenticationDAO", cascade = CascadeType.ALL)
	private CardLimitDetailsDAO cardLimitDetailsDAO;

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	public Set<JourneyPlannerRouteDAO> getJourneyPlannerRouteDAOSet() {
		return journeyPlannerRouteDAOSet;
	}

	public void setJourneyPlannerRouteDAOSet(Set<JourneyPlannerRouteDAO> journeyPlannerRouteDAOSet) {
		this.journeyPlannerRouteDAOSet = journeyPlannerRouteDAOSet;
	}

	public Set<FavouriteAddressDAO> getFavouriteAddressDAOSet() {
		return favouriteAddressDAOSet;
	}

	public void setFavouriteAddressDAOSet(Set<FavouriteAddressDAO> favouriteAddressDAOSet) {
		this.favouriteAddressDAOSet = favouriteAddressDAOSet;
	}

	public Set<FeedbackDAO> getFeedbackDAOSet() {
		return feedbackDAOSet;
	}

	public void setFeedbackDAOSet(Set<FeedbackDAO> feedbackDAOSet) {
		this.feedbackDAOSet = feedbackDAOSet;
	}

	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public DAOUser getDaoUser() {
		return daoUser;
	}

	public void setDaoUser(DAOUser daoUser) {
		this.daoUser = daoUser;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Boolean getOtpVerification() {
		return otpVerification;
	}

	public void setOtpVerification(Boolean otpVerification) {
		this.otpVerification = otpVerification;
	}

	public String getEmail() {
		return email;
	}

	public LocalDateTime getLastlogin() {
		return lastlogin;
	}

	public void setLastlogin(LocalDateTime lastlogin) {
		this.lastlogin = lastlogin;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public CardDetailsDAO getCardDetailsDAO() {
		return cardDetailsDAO;
	}

	public void setCardDetailsDAO(CardDetailsDAO cardDetailsDAO) {
		this.cardDetailsDAO = cardDetailsDAO;
	}

	public Set<TransactionDAO> getTransactionDAOS() {
		return transactionDAOS;
	}

	public void setTransactionDAOS(Set<TransactionDAO> transactionDAOS) {
		this.transactionDAOS = transactionDAOS;
	}

	public Set<TicketDAO> getTicketDAOSet() {
		return ticketDAOSet;
	}

	public void setTicketDAOSet(Set<TicketDAO> ticketDAOSet) {
		this.ticketDAOSet = ticketDAOSet;
	}

	public List<String> getRolesList() {
		List<String> userRoles = new ArrayList<>();
		roles.forEach(role -> userRoles.add(role.getName()));
		return userRoles;
	}

	public SessionDAO getSessionDAO() {
		return sessionDAO;
	}

	public void setSessionDAO(SessionDAO sessionDAO) {
		this.sessionDAO = sessionDAO;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Set<ExploreDAO> getExploreDAOS() {
		return exploreDAOS;
	}

	public void setExploreDAOS(Set<ExploreDAO> exploreDAOS) {
		this.exploreDAOS = exploreDAOS;
	}

	public String getMpin() {
		return mpin;
	}

	public void setMpin(String mpin) {
		this.mpin = mpin;
	}

	public Set<MpinLog> getMpins() {
		return mpinLogs;
	}

	public void setMpins(Set<MpinLog> mpinLogs) {
		this.mpinLogs = mpinLogs;
	}

	public Set<NotificationDAO> getNotificationDAOS() {
		return notificationDAOS;
	}

	public void setNotificationDAOS(Set<NotificationDAO> notificationDAOS) {
		this.notificationDAOS = notificationDAOS;
	}

	public Set<ExploreDAO> getExploreDAOSet() {
		return exploreDAOSet;
	}

	public void setExploreDAOSet(Set<ExploreDAO> exploreDAOSet) {
		this.exploreDAOSet = exploreDAOSet;
	}

	public Set<MpinLog> getMpinLogs() {
		return mpinLogs;
	}

	public void setMpinLogs(Set<MpinLog> mpinLogs) {
		this.mpinLogs = mpinLogs;
	}

	public Set<LoginLog> getLoginLogs() {
		return loginLogs;
	}

	public void setLoginLogs(Set<LoginLog> loginLogs) {
		this.loginLogs = loginLogs;
	}

	public CardLimitDetailsDAO getCardLimitDetailsDAO() {
		return cardLimitDetailsDAO;
	}

	public void setCardLimitDetailsDAO(CardLimitDetailsDAO cardLimitDetailsDAO) {
		this.cardLimitDetailsDAO = cardLimitDetailsDAO;
	}
}
