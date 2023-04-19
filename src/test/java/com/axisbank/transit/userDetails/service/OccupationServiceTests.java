package com.axisbank.transit.userDetails.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.userDetails.service.impl.OccupationServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.VALID_OCCUPATIONS;
import static org.mockito.Mockito.when;

public class OccupationServiceTests extends BaseTest {

    @InjectMocks
    @Autowired
    OccupationServiceImpl occupationService;

    @Mock
    GlobalConfigService globalConfigService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllOcuupationsTest() throws Exception {
        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();
        globalConfigDTO.setJson(true);
        globalConfigDTO.setValue(null);
        globalConfigDTO.setKey(VALID_OCCUPATIONS);
        String json = "{\n" +
                "  \"occupations\": [\n" +
                "    \"Bussiness Professional\",\n" +
                "    \"Medical / Healthcare Professional\",\n" +
                "    \"Government / Civil Services\",\n" +
                "    \"Retired\",\n" +
                "    \"Educator\",\n" +
                "    \"Homemaker\",\n" +
                "    \"Hospitality\",\n" +
                "    \"Transportation\",\n" +
                "    \"Sales\",\n" +
                "    \"Student\",\n" +
                "    \"Technology / Engineer\",\n" +
                "    \"Other\"\n" +
                "  ]\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        globalConfigDTO.setJsonValue(jsonNode);
        when(globalConfigService.getGlobalConfig(VALID_OCCUPATIONS, true)).thenReturn(globalConfigDTO);

        Assert.assertNotNull(occupationService.getAllOccupations());
    }
}
