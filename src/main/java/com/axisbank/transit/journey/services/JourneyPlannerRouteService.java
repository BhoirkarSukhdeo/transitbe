package com.axisbank.transit.journey.services;

import com.axisbank.transit.journey.model.DTO.JourneyPlannerConfirmedRouteDTO;
import com.axisbank.transit.journey.model.DTO.JourneyPlannerRouteDTO;

public interface JourneyPlannerRouteService {
    public void saveJourneyPlannerRoute(JourneyPlannerRouteDTO journeyPlannerRouteDTO) throws Exception;

    public JourneyPlannerConfirmedRouteDTO getJourneyPlannerRoute() throws Exception;

    public void endJourney() throws Exception;
}
