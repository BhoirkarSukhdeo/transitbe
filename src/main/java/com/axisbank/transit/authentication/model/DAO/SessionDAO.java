package com.axisbank.transit.authentication.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "auth_session")
public class SessionDAO extends BaseEntity {

    @Column(name = "session_ref_id")
    private String sessionRefId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "is_blocked")
    private boolean isBlocked = false;

    @Column(name = "user_attempts")
    private int userAttempts;

    @Column(name = "block_time")
    private LocalDateTime blockTime;

    @Column(name = "is_locked")
    private boolean isLocked = false;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name = "unlock_time")
    private LocalDateTime unLockTime;

    @Column(name = "unblock_time")
    private LocalDateTime unBlockTime;

    @Column(name = "last_api_access_time")
    private LocalDateTime lastApiAccessTime;

    @OneToOne
    @JoinColumn(name = "authentication_id")
    private AuthenticationDAO authenticationDAO;

    public String getSessionRefId() {
        return sessionRefId;
    }

    public void setSessionRefId(String sessionRefId) {
        this.sessionRefId = sessionRefId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public int getUserAttempts() {
        return userAttempts;
    }

    public void setUserAttempts(int userAttempts) {
        this.userAttempts = userAttempts;
    }

    public AuthenticationDAO getAuthenticationDAO() {
        return authenticationDAO;
    }

    public void setAuthenticationDAO(AuthenticationDAO authenticationDAO) {
        this.authenticationDAO = authenticationDAO;
    }

    public LocalDateTime getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(LocalDateTime blockTime) {
        this.blockTime = blockTime;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public LocalDateTime getLockTime() {
        return lockTime;
    }

    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }

    public LocalDateTime getUnLockTime() {
        return unLockTime;
    }

    public void setUnLockTime(LocalDateTime unLockTime) {
        this.unLockTime = unLockTime;
    }

    public LocalDateTime getUnBlockTime() {
        return unBlockTime;
    }

    public void setUnBlockTime(LocalDateTime unBlockTime) {
        this.unBlockTime = unBlockTime;
    }

    public LocalDateTime getLastApiAccessTime() {
        return lastApiAccessTime;
    }

    public void setLastApiAccessTime(LocalDateTime lastApiAccessTime) {
        this.lastApiAccessTime = lastApiAccessTime;
    }
}
