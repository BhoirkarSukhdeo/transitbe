package com.axisbank.transit.core.shared.utils;

import java.util.*;

import static com.axisbank.transit.kmrl.constant.WorkFlowConstants.*;

public class WorkFlowUtil {
    private static Map<String, List<String>> statusStates;

    static {
        statusStates = new HashMap<>();
        statusStates.put(CREATED, new ArrayList<>(Arrays.asList(APPROVED, REJECTED)));
        statusStates.put(APPROVED, new ArrayList<>(Arrays.asList(PUBLISHED, REJECTED)));
        statusStates.put(REJECTED, new ArrayList<>());
        statusStates.put(PUBLISHED, new ArrayList<>());
    }
    public static boolean verifyWorkFlow(String currStatus, String nextStatus){
        if(!validateStatusChange(currStatus, nextStatus))
            return false;
        switch (currStatus){
            case CREATED:
                return WorkFlowPermissionUtil.changeStatusToCreate();
            case APPROVED:
                return WorkFlowPermissionUtil.changeStatusToApproved();
            case REJECTED:
                return WorkFlowPermissionUtil.changeStatusToRejected();
            case PUBLISHED:
                return WorkFlowPermissionUtil.changeStatusToPublished();
            default:
                return false;
        }
    }
    public static boolean validateStatusChange(String initialStatus, String updatedStatus){
        return updatedStatus.equalsIgnoreCase(CREATED) || statusStates.get(initialStatus).contains(updatedStatus);
    }
}
