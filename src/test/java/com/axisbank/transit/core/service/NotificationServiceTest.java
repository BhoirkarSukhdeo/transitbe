package com.axisbank.transit.core.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DAO.MpinLog;
import com.axisbank.transit.core.model.DAO.NotificationDAO;
import com.axisbank.transit.core.model.response.AddNotificationDTO;
import com.axisbank.transit.core.model.response.NotificationDTO;
import com.axisbank.transit.core.repository.NotificationRepository;
import com.axisbank.transit.core.service.impl.NotificationServiceImpl;
import com.axisbank.transit.core.shared.constants.NotificationConstants;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.PushNotificationUtil;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.model.DAO.DeviceInfo;
import com.axisbank.transit.userDetails.util.UserUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static com.axisbank.transit.core.shared.constants.NotificationConstants.SUCCESS_STATUS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CommonUtils.class)
public class NotificationServiceTest extends BaseTest {
    private AuthenticationDAO authenticationDAO;
    private DAOUser daoUser;
    NotificationDAO notificationDAO;
    List<NotificationDAO> notificationDAOList;
    AddNotificationDTO addNotificationDTO;

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    UserUtil userUtil;

    @Mock
    PushNotificationUtil pushNotificationUtil;

    @InjectMocks
    @Autowired
    NotificationServiceImpl notificationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CommonUtils.class);

        authenticationDAO = new AuthenticationDAO();

        daoUser = new DAOUser();
        daoUser.setOccupation("SE");
        daoUser.setDob(LocalDate.of(1994, 11, 23));
        daoUser.setGender(Gender.MALE);

        authenticationDAO.setMobile("8899899709");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);
        Set<ExploreDAO> exploreDAOSet = new HashSet<>();
        authenticationDAO.setExploreDAOSet(exploreDAOSet);
        Set<MpinLog> mpinLogSet = new HashSet<>();
        authenticationDAO.setMpins(mpinLogSet);
        authenticationDAO.setDaoUser(daoUser);

        notificationDAO = new NotificationDAO();
        notificationDAO.setSeen(true);
        notificationDAO.setStatus(SUCCESS_STATUS);
        notificationDAO.setTitle("Info");
        notificationDAO.setSubTitle("SunInfo");
        notificationDAO.setNotificationRefId("not123");
        notificationDAO.setSubTitle("offer");
        notificationDAO.setType("xyz");
        notificationDAO.setTypeId("1");
        notificationDAO.setBody("body");
        notificationDAO.setBannerUrl("www.banner.com");
        notificationDAO.setAction("send");
        notificationDAO.setCreationDateTime(new Date());

        notificationDAOList = new ArrayList<>();
        notificationDAOList.add(notificationDAO);

        addNotificationDTO = new AddNotificationDTO();
        addNotificationDTO.setExpiryDate(new Date());
        addNotificationDTO.setSeen(true);
        addNotificationDTO.setStatus(SUCCESS_STATUS);
        addNotificationDTO.setTitle("Info");
        addNotificationDTO.setSubTitle("SunInfo");
        addNotificationDTO.setSubTitle("offer");
        addNotificationDTO.setType("xyz");
        addNotificationDTO.setTypeId("1");
        addNotificationDTO.setBody("body");
        addNotificationDTO.setBannerUrl("www.banner.com");
        addNotificationDTO.setAction("send");

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setFcmToken("fcm123");
        authenticationDAO.setDeviceInfo(deviceInfo);

        addNotificationDTO.setAuthenticationDAO(authenticationDAO);


    }

    @Test
    public void getNotificationsTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);

        when(notificationRepository.findAllByAuthenticationDAO(any(AuthenticationDAO.class), any(Pageable.class))).thenReturn(notificationDAOList);
        Assert.assertNotNull(notificationService.getNotifications(0, 10));
    }

    @Test
    public void getNotificationDetailTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(notificationRepository.findByNotificationRefIdAndAuthenticationDAO(any(String.class),any(AuthenticationDAO.class))).thenReturn(notificationDAO);
        Assert.assertNotNull(notificationService.getNotificationDetail("123"));
    }

    @Test
    public void saveNotificationTest() throws Exception {
        when(notificationRepository.save(any(NotificationDAO.class))).thenReturn(notificationDAO);
        when(pushNotificationUtil.sendPushNotification(any(String.class), any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn("sent");
        notificationService.saveNotification(addNotificationDTO);
    }

    @Test
    public void saveNotificationTest2() throws Exception {
        List<AuthenticationDAO> authenticationDAOS = new ArrayList<>();
        authenticationDAOS.add(authenticationDAO);
        List<NotificationDAO> notificationDAOS = new ArrayList<>();
        notificationDAOS.add(notificationDAO);
        PowerMockito.when(CommonUtils.generateRandomString(any(Integer.class))).thenReturn("12345");
        when(notificationRepository.saveAll(anyList())).thenReturn(notificationDAOS);
        when(pushNotificationUtil.sendPushNotification(any(String.class), any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn("sent");
        notificationService.saveNotification(authenticationDAOS, addNotificationDTO);
    }

    @Test
    public void getAddNotificationDTOForTopUpTest() throws Exception {
        Assert.assertNotNull(notificationService.getAddNotificationDTOForTopUp("123", NotificationConstants.SUCCESS_STATUS,23.0,authenticationDAO));
    }

    @Test
    public void getAddNotificationDTOForTopUpTest2() throws Exception {
        Assert.assertNotNull(notificationService.getAddNotificationDTOForTopUp("123", NotificationConstants.FAIL_STATUS,23.0,authenticationDAO));
    }

    @Test
    public void getAddNotificationDTOForPurchaseTicketTest() throws Exception {
        Assert.assertNotNull(notificationService.getAddNotificationDTOForPurchaseTicket("123", NotificationConstants.FAIL_STATUS,"ALUVA",authenticationDAO));
    }

    @Test
    public void getAddNotificationDTOForPurchaseTicketTest2() throws Exception {
        Assert.assertNotNull(notificationService.getAddNotificationDTOForPurchaseTicket("123", NotificationConstants.SUCCESS_STATUS,"ALUVA",authenticationDAO));
    }
}
