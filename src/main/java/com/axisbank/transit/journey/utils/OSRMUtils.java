package com.axisbank.transit.journey.utils;

import com.axisbank.transit.journey.model.DTO.CoordinatesDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.axisbank.transit.core.shared.constants.UtilsConstants.OSRM_DISTANCE_PROFILE;
import static com.axisbank.transit.core.shared.utils.HttpClientUtils.httpGetRequest;

@Slf4j
@Component
public class OSRMUtils {

    @Value("${app.osrm.base_url}")
    private String osrmBaseUrl;

    @Autowired
    private ObjectMapper mapper;
    public List<Map<String,Double>> getOSRMDistance(CoordinatesDto source, List<CoordinatesDto> destinations) {
        List<Map<String, Double>> distances = new ArrayList<>();
        if (destinations.isEmpty()){
            log.info("Empty List of destinations found");
            return distances;
        }
        try {
            System.out.println(prepareUrlForOSRMRequest(source, destinations));
                ResponseEntity<String> response = httpGetRequest(prepareUrlForOSRMRequest(source, destinations));
                if (response.getStatusCode().is2xxSuccessful()) {
                    JsonNode outerNode = mapper.readTree(response.getBody());
                    ArrayNode routes = (ArrayNode) outerNode.get("routes");
                    ObjectNode innerNode = (ObjectNode) routes.get(0);
                    ArrayNode legs = (ArrayNode) innerNode.get("legs");
                    for (int i = 0; i < legs.size(); i++) {
                        if (i % 2 == 0) {
                            Map<String, Double> distObj = new HashMap<>();
                            Double dist = Double.valueOf(legs.get(i).get("distance").toString());
                            Double time = Double.valueOf(legs.get(i).get("duration").toString());
                            distObj.put("distance",dist);
                            distObj.put("duration",time);
                            distances.add(distObj);
                        }
                    }
                }
            return distances;
        } catch (Exception e) {
            log.error("Error fetching OSRM distance on Partitioned collection{}",e.toString());
            return distances;
        }
    }

    private String prepareUrlForOSRMRequest(CoordinatesDto source, List<CoordinatesDto> destinations) {

        log.info("Source Lat Lon are: {} and {}", source.getLatitude(), source.getLongitude());
        String url = osrmBaseUrl + "/" + OSRM_DISTANCE_PROFILE + "/";
        StringBuilder builder = new StringBuilder();
        for (CoordinatesDto destination : destinations) {
            builder.append(source.getLongitude());
            builder.append(",");
            builder.append(source.getLatitude());
            builder.append(";");
            builder.append(destination.getLongitude());
            builder.append(",");
            builder.append(destination.getLatitude());
            builder.append(";");
        }
        builder.deleteCharAt(builder.length() - 1);
        url = url + builder.toString();
        return url;
    }
}
