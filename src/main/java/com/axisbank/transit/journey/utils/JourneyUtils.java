package com.axisbank.transit.journey.utils;

import static com.axisbank.transit.journey.constants.JourneyConstants.WALKING_SPEED;

public class JourneyUtils {
    public static Double getDurationFromDistance(Double distance){
        Double speed = WALKING_SPEED;
        try{
            return distance/speed;
        } catch (Exception e)
        {
            return 0.0;
        }
    }
}
