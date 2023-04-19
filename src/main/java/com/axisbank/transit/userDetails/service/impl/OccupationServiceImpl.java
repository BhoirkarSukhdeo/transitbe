package com.axisbank.transit.userDetails.service.impl;

import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.userDetails.model.DTO.OccupationDTO;
import com.axisbank.transit.userDetails.service.OccupationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.VALID_OCCUPATIONS;

@Slf4j
@Service
public class OccupationServiceImpl implements OccupationService {
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    GlobalConfigService globalConfigService;

    @Override
    public List<OccupationDTO> getAllOccupations() throws Exception {
        log.info("Request receive in getAllOccupations method");
        List<OccupationDTO> result = new ArrayList<>();
        try {
            GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(VALID_OCCUPATIONS, true);
            if (globalConfig != null) {
                JsonNode genderNode = globalConfig.getJsonValue();
                List<String> occupations= mapper.convertValue(genderNode.get("occupations"), new TypeReference<List<String>>(){});
                for (int i=0; i<occupations.size(); i++) {
                    OccupationDTO occupationDTO = new OccupationDTO();
                    occupationDTO.setOccupationId(String.valueOf(i+1));
                    occupationDTO.setDisplayName(occupations.get(i));
                    result.add(occupationDTO);
                }

            }
        } catch (Exception exception) {
            log.error("Error in getAllOccupations: {}", exception.getMessage());
            throw new Exception("Error in getting occupation list, Please try again later");
        }
        return result;
    }
}
