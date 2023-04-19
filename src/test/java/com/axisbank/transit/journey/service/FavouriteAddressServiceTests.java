package com.axisbank.transit.journey.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.journey.model.DAO.FavouriteAddressDAO;
import com.axisbank.transit.journey.model.DTO.FavouriteAddressDTO;
import com.axisbank.transit.journey.repository.FavouriteAddressRepository;
import com.axisbank.transit.journey.services.impl.FavouriteAddressServiceImpl;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.util.UserUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FavouriteAddressServiceTests extends BaseTest {
    AuthenticationDAO authenticationDAO;
    DAOUser daoUser;
    FavouriteAddressDAO favouriteAddressDAO;
    FavouriteAddressDTO favouriteAddressDTO;
    List<FavouriteAddressDAO> favouriteAddressDAOList;

    @Mock
    UserUtil userUtil;

    @Mock
    FavouriteAddressRepository favouriteAddressRepository;

    @InjectMocks
    @Autowired
    FavouriteAddressServiceImpl favouriteAddressService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        authenticationDAO = new AuthenticationDAO();
        daoUser = new DAOUser();
        daoUser.setOccupation("SE");
        daoUser.setDob(LocalDate.of(1994, 11, 23));
        daoUser.setGender(Gender.MALE);

        authenticationDAO.setMobile("2233771199");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);
        authenticationDAO.setDaoUser(daoUser);

        favouriteAddressDAO = new FavouriteAddressDAO();
        favouriteAddressDAO.setAddressId("123");
        favouriteAddressDAO.setAddress("add");
        favouriteAddressDAO.setAuthenticationDAO(authenticationDAO);
        favouriteAddressDAO.setFavouriteType("home");
        favouriteAddressDAO.setAddressTitle("title");
        favouriteAddressDAO.setLatitute(23.5);
        favouriteAddressDAO.setLongitude(34.6);

        favouriteAddressDTO = new FavouriteAddressDTO();
        favouriteAddressDTO.setAddress("abc");
        favouriteAddressDTO.setAddressId("123");
        favouriteAddressDTO.setAddressTitle("title");
        favouriteAddressDTO.setLatitute(23.4);
        favouriteAddressDTO.setLongitude(23.8);

        favouriteAddressDAOList = new ArrayList<>();
        favouriteAddressDAOList.add(favouriteAddressDAO);
    }

    @Test
    public void saveFavouriteAddressTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(favouriteAddressRepository.save(any(FavouriteAddressDAO.class))).thenReturn(favouriteAddressDAO);
        favouriteAddressService.saveFavouriteAddress(favouriteAddressDTO);
    }

    @Test
    public void getFavouriteAddressesTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(favouriteAddressRepository.findAllByFavouriteTypeAndAuthenticationDAO(any(String.class), any(AuthenticationDAO.class), any(Pageable.class))).thenReturn(favouriteAddressDAOList);
        when(favouriteAddressRepository.findAllByAuthenticationDAO(any(AuthenticationDAO.class), any(Pageable.class))).thenReturn(favouriteAddressDAOList);
        Assert.assertNotNull(favouriteAddressService.getFavouriteAddresses(0, 10, "abc"));
    }

    @Test
    public void deleteFavouriteAddressTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(favouriteAddressRepository.findByAddressId(any(String.class))).thenReturn(favouriteAddressDAO);
        doNothing().when(favouriteAddressRepository).deleteById(any(Long.class));
        favouriteAddressService.deleteFavouriteAddress("xyz");
    }
}
