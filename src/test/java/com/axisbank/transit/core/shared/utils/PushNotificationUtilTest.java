package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.core.service.PushNotificationsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class PushNotificationUtilTest extends BaseTest {

    @InjectMocks
    @Autowired
    PushNotificationUtil pushNotificationUtil;

    @Mock
    PushNotificationsService pushNotificationsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendPushNotificationTest() throws Exception {
        when(pushNotificationsService.send(any())).thenReturn(any());
        Assert.assertNotNull(pushNotificationUtil.sendPushNotification("token","body","title","subTitle","action"));
    }
}
