package com.axisbank.transit.authentication.config;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.AuditRevisionEntity;
import com.axisbank.transit.userDetails.util.UserUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.RevisionListener;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class AuditRevisionListener implements RevisionListener {

    @Autowired
    UserUtil userUtil;

    @SneakyThrows
    @Override
    public void newRevision(Object revisionEntity) {
        String username = null;
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            username = authenticationDAO.getUserName();
        } catch (Exception exception) {
            log.error("unable to find loggedIn user");
        }
        AuditRevisionEntity audit = (AuditRevisionEntity) revisionEntity;
        audit.setUpdatedByUsername(username);
    }
}
