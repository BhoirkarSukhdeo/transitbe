package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.core.shared.constants.RoleConstants;
import org.springframework.security.access.annotation.Secured;

public class WorkFlowPermissionUtil {
    @Secured({RoleConstants.MAKER, RoleConstants.ADMIN_ROLE})
    public static boolean changeStatusToCreate() {
        return true;
    }

    @Secured({RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE})
    public static boolean changeStatusToRejected() {
        return true;
    }

    @Secured({RoleConstants.CHECKER, RoleConstants.ADMIN_ROLE})
    public static boolean changeStatusToApproved() {
        return true;
    }

    @Secured({RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE})
    public static boolean changeStatusToPublished() {
        return true;
    }
}
