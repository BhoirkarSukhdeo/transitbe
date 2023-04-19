package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.BaseTest;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class DijkstrasAlgorithmTest extends BaseTest {

    @InjectMocks
    @Autowired
    DijkstrasAlgorithm dijkstrasAlgorithm;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
}
