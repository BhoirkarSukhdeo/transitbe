package com.axisbank.transit.explore.aspect;

import com.axisbank.transit.core.service.NotificationService;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.service.ExploreService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.axisbank.transit.explore.shared.constants.ExploreStatus.PUBLISHED;


@Slf4j
@Aspect
@Configuration
public class ExploreAspect {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ExploreService exploreService;
    @After(value = "execution(* com.axisbank.transit.explore.repository.ExploreRepository.save(..))")
    public void afterSaveTransaction(JoinPoint joinPoint) throws Exception {
        log.info("request receive for sending message after publishing explore method call using AOP");
        try {
            Object[] allArgs = joinPoint.getArgs();
            ExploreDAO exploreDAO = (ExploreDAO) allArgs[0];
            String currentStatus = exploreDAO.getCurrentStatus();
            if(!currentStatus.equalsIgnoreCase(PUBLISHED))
                return;
            exploreService.pushNotifications(exploreDAO);
        } catch (Exception ex) {
            log.error("Failed to send explore notification: {}", ex.getMessage());
        }
    }
}