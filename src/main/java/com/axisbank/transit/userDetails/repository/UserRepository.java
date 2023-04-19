package com.axisbank.transit.userDetails.repository;

import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<DAOUser, Long> {

	DAOUser findByUserIdAndIsActive(String userId, Boolean isActive);
	DAOUser findByUserId(String userId);
	List<DAOUser> findByUserIdIn(List<String> userIds);
	List<DAOUser> findByDobIn(List<LocalDate> dobList);

    List<DAOUser> findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationIn(LocalDate minDob, LocalDate maxDob, List<Gender> genderList, List<String> occupationList);

    List<DAOUser> findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationInAndAuthenticationDAO_CardDetailsDAOIsNotNull(LocalDate minDob, LocalDate maxDob, List<Gender> genderList, List<String> occupationList);

	List<DAOUser> findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationInAndAuthenticationDAO_CardDetailsDAOIsNull(LocalDate minDob, LocalDate maxDob, List<Gender> genderList, List<String> occupationList);
	DAOUser findByAuthenticationDAO_UserName(String username);

	List<DAOUser> findAllByAuthenticationDAO_UserTypeAndOccupationIsNotNull(String userType);

	@Query(value = "select distinct occupation from user_detail", nativeQuery = true)
	List<String> getDistinctOccupations();
}