package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.core.service.PushNotificationsService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PushNotificationUtil {
    @Autowired
    PushNotificationsService pushNotificationsService;

    /**
     * Takes Single device registration ID and send notification on that device
     * @param token
     * @param body
     * @param title
     * @param subTitle
     * @return
     * @throws Exception
     */
    public String sendPushNotification(String token, String body, String title, String subTitle, String action) throws Exception {
        HttpEntity<String> req = createRequestObj(token, body, title, subTitle, action);
        return sendNotification(req);
    }

    /**
     * Takes List of device IDs and send notification to all devices
     * @param tokens
     * @param body
     * @param title
     * @param subTitle
     * @return
     * @throws Exception
     */
    public String sendPushNotification(List<String> tokens, String body, String title, String subTitle, String action) throws Exception {
        HttpEntity<String> req = createRequestObj(tokens, body, title, subTitle, action);
        return sendNotification(req);
    }

    /**
     * Sends Notification for prepared request
     * @param req
     * @return
     * @throws Exception
     */
    private String sendNotification(HttpEntity<String> req){
        pushNotificationsService.send(req);
        return "";
    }

    /**
     * Creates Json object for single device
     * @param token
     * @param body
     * @param title
     * @param subTitle
     * @return
     */
    public HttpEntity<String> createRequestObj(String token, String body, String title, String subTitle, String action){
        JSONObject request = generateJsonObject(body, title, subTitle, action);
        request.put("to",token);
        return new HttpEntity<>(request.toString());
    }

    /**
     * Creates Json Object for multiple devices
     * @return
     */
    private HttpEntity<String> createRequestObj(List<String> tokens, String body, String title, String subTitle, String action){
        JSONObject request = generateJsonObject(body, title, subTitle, action);
        request.put("registration_ids", tokens);
        return new HttpEntity<>(request.toString());
    }

    /**
     * Creates Request body for push notification
     * @param body
     * @param title
     * @param subTitle
     * @return
     */
    private JSONObject generateJsonObject(String body, String title, String subTitle, String action){
        JSONObject request = new JSONObject();
        request.put("priority", "high");
        request.put("content_available", true);
        request.put("mutable_content", true);

        JSONObject notification = new JSONObject();
        notification.put("body", body);
        notification.put("title", title);
        notification.put("subtitle", subTitle);
        notification.put("color", "cyan");

        JSONObject data = new JSONObject();
        data.put("click_action", action);
        data.put("body", body);
        data.put("title", title);

        request.put("notification",notification);
        request.put("data", data);
        return request;
    }
}
