package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class BaseResponseTypeTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void errorResponseTest() {
        Assert.assertNotNull(BaseResponseType.errorResponse(400, "Error"));
    }

    @Test
    public void errorResponseTest2() {
        Assert.assertNotNull(BaseResponseType.errorResponse("Error"));
    }

    @Test
    public void successfulResponseTest() {
        Assert.assertNotNull(BaseResponseType.successfulResponse(200, "Error"));
    }

    @Test
    public void successfulResponseTest2() {
        Assert.assertNotNull(BaseResponseType.successfulResponse("Error"));
    }

    @Test
    public void forbiddenResponseTest() {
        Assert.assertNotNull(BaseResponseType.forbiddenResponse(403, "Error"));
    }

    @Test
    public void forbiddenResponseTest2() {
        Assert.assertNotNull(BaseResponseType.forbiddenResponse("Error"));
    }
}
