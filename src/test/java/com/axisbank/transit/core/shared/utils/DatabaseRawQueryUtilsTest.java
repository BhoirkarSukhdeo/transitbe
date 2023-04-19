package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseRawQueryUtilsTest extends BaseTest {

    @InjectMocks
    @Autowired
    DatabaseRawQueryUtils databaseRawQueryUtils;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void generateGeomFromGeoJSONTest() throws Exception {
        databaseRawQueryUtils.generateGeomFromGeoJSON("xyz");
    }
}
