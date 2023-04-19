package com.axisbank.transit.core.filters;


import com.axisbank.transit.core.shared.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Component
@Order(2)
public class ProcessRequestFilter implements Filter {
    /**
     * This Filter Allows you to perform operation on API request such as logging time taken by API,
     * validating API requests,etc
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        long time = CommonUtils.getCurrentTimeSec();
        HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) servletResponse);
        try {
            filterChain.doFilter(servletRequest, responseCopier);
            responseCopier.flushBuffer();
        } finally {
            time = CommonUtils.getCurrentTimeSec() - time;
            byte[] copy = responseCopier.getCopy();
            String urlString=((HttpServletRequest) servletRequest).getRequestURI();
            String responseBody = new String(copy, servletResponse.getCharacterEncoding());
            log.info("API: {}, execution time: {}s, status code: {}, httpMethod: {}, ServerName: {}", urlString,  time, ((HttpServletResponse) servletResponse).getStatus(),
                    ((HttpServletRequest) servletRequest).getMethod(), servletRequest.getServerName());
            log.info("API:{}, Response: {}",urlString, responseBody);
        }
    }

}
