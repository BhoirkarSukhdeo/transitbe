package com.axisbank.transit.payment.config;

import in.juspay.model.JuspayEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PaymentConfig {

    @Value("${juspay.api_key}")
    private String juspayApiKey;

    @Value("${juspay.merchant_id}")
    private String merchantId;

    @Value("${juspay.payment.url}")
    private String juspayUrl;

    @Value("${app.proxy.host}")
    String proxyHost;

    @Value("${app.proxy.port}")
    String proxyPort;

    @Value("${app.proxy.nonProxy}")
    String nonProxyList;

    /**
     * Environment Properties Initialization
     * @param
     * @return
     * @throws
     */

    @PostConstruct
    public void init() {
        JuspayEnvironment.withBaseUrl(juspayUrl);
        JuspayEnvironment.withApiKey(juspayApiKey);
        JuspayEnvironment.withMerchantId(merchantId);
        // Bellow settings are for external requests via Proxy server.
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
        System.setProperty("http.nonProxyHosts", nonProxyList);
    }
}
