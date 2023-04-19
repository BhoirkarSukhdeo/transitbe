package com.axisbank.transit.explore.util;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.model.DAO.SlotDAO;
import com.axisbank.transit.explore.model.DTO.ExploreFilterDTO;
import com.axisbank.transit.explore.model.DTO.TargetAudienceDTO;
import com.axisbank.transit.explore.model.DTO.TargetOptionDTO;
import com.axisbank.transit.explore.repository.ExploreRepository;
import com.axisbank.transit.explore.shared.constants.ExploreStatus;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.axisbank.transit.explore.constants.CustomizedTargettingName.*;
import static com.axisbank.transit.explore.constants.LevelOneFilters.*;
import static com.axisbank.transit.explore.constants.LevelTwoFilters.*;
import static com.axisbank.transit.userDetails.constants.Gender.NA;

@Slf4j
@Component
public class ChangeExploreStatusUtil {

    private static Map<String, List<String>> statusStates;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    ExploreRepository exploreRepository;

    static {
        statusStates = new HashMap<>();
        statusStates.put(ExploreStatus.CREATED, new ArrayList<>(Arrays.asList(ExploreStatus.APPROVED, ExploreStatus.REJECTED)));
        statusStates.put(ExploreStatus.APPROVED, new ArrayList<>(Arrays.asList(ExploreStatus.PUBLISHED, ExploreStatus.REJECTED)));
        statusStates.put(ExploreStatus.REJECTED, new ArrayList<>());
        statusStates.put(ExploreStatus.PUBLISHED, new ArrayList<>());
    }

    private ExploreDAO changeStatus(ExploreDAO exploreDAO, AuthenticationDAO updatedBy, String updatedStatus){
        exploreDAO.setCurrentStatus(updatedStatus);
        exploreDAO.setAuthenticationDAO(updatedBy);
        return exploreDAO;
    }

    public boolean validateStatusChange(String initialStatus, String updatedStatus){
        return updatedStatus.equalsIgnoreCase(ExploreStatus.CREATED)? true: statusStates.get(initialStatus).contains(updatedStatus);
    }

    @Secured({RoleConstants.MAKER, RoleConstants.ADMIN_ROLE})
    public ExploreDAO changeStatusToCreate(ExploreDAO exploreDAO, AuthenticationDAO updatedBy) {
        return changeStatus(exploreDAO, updatedBy ,ExploreStatus.CREATED);
    }

    @Secured({RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE})
    public ExploreDAO changeStatusToRejected(ExploreDAO exploreDAO, AuthenticationDAO updatedBy) throws Exception {
        if (exploreDAO.getCurrentStatus().equalsIgnoreCase(ExploreStatus.APPROVED)) {
            if (updatedBy.getRolesList().contains(RoleConstants.PUBLISHER) || updatedBy.getRolesList().contains(RoleConstants.ADMIN_ROLE)) {
                return changeStatus(exploreDAO, updatedBy ,ExploreStatus.REJECTED);
            } else {
                throw new Exception("You are not allowed to reject this Explore Item");
            }
        }
        return changeStatus(exploreDAO, updatedBy ,ExploreStatus.REJECTED);
    }

    @Secured({RoleConstants.CHECKER, RoleConstants.ADMIN_ROLE})
    public ExploreDAO changeStatusToApproved(ExploreDAO exploreDAO, AuthenticationDAO updatedBy) throws Exception {
        if (exploreDAO.getAuthenticationDAO() != null) {
            if (exploreDAO.getAuthenticationDAO().getId() == updatedBy.getId()) {
                throw new Exception("You can not approve the Explore Item created by you.");
            }
        }
        verifyExpiryDate(exploreDAO);
        return changeStatus(exploreDAO, updatedBy ,ExploreStatus.APPROVED);
    }

    @Secured({RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE})
    public ExploreDAO changeStatusToPublished(ExploreDAO exploreDAO, AuthenticationDAO updatedBy) throws Exception {
        if (exploreDAO.getAuthenticationDAO() != null) {
            if (exploreDAO.getAuthenticationDAO().getId() == updatedBy.getId()) {
                throw new Exception("You can not publish the Explore Item approved by you.");
            }
        }
        verifyExpiryDate(exploreDAO);
        TargetAudienceDTO targetAudience = null;
        try {
            targetAudience = exploreDAO.getTargetAudience();
        } catch (Exception exception) {
            log.error("Exception in getting target audience: {}", exception.getMessage());
            throw new Exception("Target Audience Missing");
        }

        for (TargetOptionDTO targetOptionDTO : targetAudience.getData()) {
            switch (targetOptionDTO.getFilter()) {
                case CUSTOMISED_TARGETTING:
                    ExploreFilterDTO exploreFilterDTO = targetOptionDTO.getSubFilters().get(0);
                    Set<AuthenticationDAO> authenticationDAOSet = new HashSet<>();
                    switch (exploreFilterDTO.getSelectedVal()) {
                        case TRANSIT_APP_ID:
                            List<DAOUser> daoUserList = userRepository.findByUserIdIn(exploreFilterDTO.getListVals());
                            for (DAOUser daoUser: daoUserList) {
                                daoUser.getAuthenticationDAO().getExploreDAOSet().add(exploreDAO);
                                authenticationDAOSet.add(daoUser.getAuthenticationDAO());
                            }
                            exploreDAO.setAuthenticationDAOSet(authenticationDAOSet);
                            break;
                        case MOBILE_NUMBER:
                            List<AuthenticationDAO> authenticationDAOList = authenticationRepository.findByMobileIn(exploreFilterDTO.getListVals());
                            for (AuthenticationDAO authenticationDAO: authenticationDAOList) {
                                authenticationDAO.getExploreDAOSet().add(exploreDAO);
                                authenticationDAOSet.add(authenticationDAO);
                            }
                            exploreDAO.setAuthenticationDAOSet(authenticationDAOSet);
                            break;
                        case DOB:
                            List<LocalDate> dobList = exploreFilterDTO.getListVals().stream().map(v -> CommonUtils.getLocalDate(v, "dd/MM/yyyy")).collect(Collectors.toList());
                            List<DAOUser> daoUsers = userRepository.findByDobIn(dobList);
                            for (DAOUser daoUser: daoUsers) {
                                daoUser.getAuthenticationDAO().getExploreDAOSet().add(exploreDAO);
                                authenticationDAOSet.add(daoUser.getAuthenticationDAO());
                            }
                            exploreDAO.setAuthenticationDAOSet(authenticationDAOSet);
                            break;
                    }
                    break;
                case ALL_KOCHI1_CARD_USERS:
                    exploreDAO = addGenderAgeOccupationFilter(targetOptionDTO, exploreDAO, ALL_KOCHI1_CARD_USERS);
                    break;
                case APP_ONLY_USERS:
                    exploreDAO = addGenderAgeOccupationFilter(targetOptionDTO, exploreDAO, APP_ONLY_USERS);
                    break;
                case ALL_USERS:
                    exploreDAO = addGenderAgeOccupationFilter(targetOptionDTO, exploreDAO, ALL_USERS);
                    break;
            }
        }
        return changeStatus(exploreDAO, updatedBy ,ExploreStatus.PUBLISHED);
    }

    private void verifyExpiryDate(ExploreDAO exploreDAO) throws Exception {
        if (exploreDAO.getSlotDAOSet() !=null && !exploreDAO.getSlotDAOSet().isEmpty()) {
            SortedSet<SlotDAO> sortedSlotDAO = new TreeSet<>(Comparator.comparing(SlotDAO::getStartDate));
            for (SlotDAO slotDAO: exploreDAO.getSlotDAOSet()) {
                sortedSlotDAO.add(slotDAO);
            }
            LocalDate endDate = sortedSlotDAO.last().getEndDate();
            String endTime = sortedSlotDAO.last().getEndTime();
            if (CommonUtils.checkIfOfferExpired(endDate, endTime)) {
                throw new Exception("Explore Item is already expired");
            }
        }
    }

    private ExploreDAO addGenderAgeOccupationFilter(TargetOptionDTO targetOptionDTO, ExploreDAO exploreDAO, String levelOneFilter) {
        Set<AuthenticationDAO> authenticationDAOSet = new HashSet<>();
        List<Gender> genderList = new ArrayList<>();
        List<String> occupationList = new ArrayList<>();
        Double minAge = 0.0, maxAge = 0.0;
        for (ExploreFilterDTO filterDTO: targetOptionDTO.getSubFilters()) {
            switch (filterDTO.getName()) {
                case AGE:
                    minAge = filterDTO.getMinVal();
                    maxAge = filterDTO.getMaxVal();
                    break;
                case OCCUPATION:
                    occupationList = filterDTO.getListVals();
                    break;
                case GENDER:
                    genderList = filterDTO.getListVals().stream().map(Gender::valueOf).collect(Collectors.toList());
                    genderList.add(NA);
                    break;
            }
        }
        LocalDate currentDate = LocalDate.now();
        LocalDate minDob = currentDate.minusYears(maxAge.longValue());
        LocalDate maxDob = currentDate.minusYears(minAge.longValue());

        List<DAOUser> daoUsers = new ArrayList<>();
        log.info("Target Aud Filters. User:{}, MinDob:{}, MaxDob:{}, genderList:{}, Ocupation:{}", levelOneFilter, minDob,
                maxDob, genderList, occupationList);
        switch (levelOneFilter) {
            case ALL_USERS:
                daoUsers = userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationIn(minDob, maxDob, genderList, occupationList);
                break;
            case ALL_KOCHI1_CARD_USERS:
                daoUsers = userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationInAndAuthenticationDAO_CardDetailsDAOIsNotNull(minDob, maxDob, genderList, occupationList);
                break;
            case APP_ONLY_USERS:
                daoUsers = userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationInAndAuthenticationDAO_CardDetailsDAOIsNull(minDob, maxDob, genderList, occupationList);
                break;
        }

        for (DAOUser daoUser: daoUsers) {
            daoUser.getAuthenticationDAO().getExploreDAOSet().add(exploreDAO);
            authenticationDAOSet.add(daoUser.getAuthenticationDAO());
            log.info("Mapped User user name:{}",daoUser.getFirstName()+daoUser.getLastName());
        }
        exploreDAO.setAuthenticationDAOSet(authenticationDAOSet);
        return exploreDAO;
    }
}
