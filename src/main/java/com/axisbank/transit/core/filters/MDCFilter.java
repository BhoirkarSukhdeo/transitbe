package com.axisbank.transit.core.filters;

import com.axisbank.transit.userDetails.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@Component
@Order(1)
@Slf4j
public class MDCFilter implements Filter {
    /**
     * Adds trace ID to every API Request using Mapped Diagnostic Context (MDC)
     * which is useful for tracing logs for API requests.
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Autowired
    UserUtil userUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;

        String traceId = req.getHeader("x-trace-id");
        if (traceId == null || StringUtils.isEmpty(traceId)) {
            MDC.put("x-trace-id", UUID.randomUUID().toString());
        }
        String ip = Optional.ofNullable(req.getHeader("X-FORWARDED-FOR")).orElse(req.getRemoteAddr());
        if (ip.equals("0:0:0:0:0:0:0:1")) ip = "127.0.0.1";
        MDC.put("x-source-ip", ip);
        String authToken = req.getHeader("authorization");
        if(authToken!=null && !authToken.equalsIgnoreCase("")){
            String userId = "";
            try{
                authToken = authToken.split(" ")[1];
                userId = userUtil.getUserIdFromToken(authToken);
            } catch (Exception ex){
                log.debug("Failed to get loggedin user");
            }
            MDC.put("x-userId", userId);
        }
        filterChain.doFilter(servletRequest, servletResponse);
        MDC.clear();
    }
}
