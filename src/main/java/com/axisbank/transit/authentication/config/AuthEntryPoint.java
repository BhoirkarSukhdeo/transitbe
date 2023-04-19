package com.axisbank.transit.authentication.config;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class AuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res,
                         AuthenticationException ex) throws IOException, ServletException {
        res.setHeader("Content-Type","application/json");
        res.setStatus(HttpStatus.FORBIDDEN.value());
        BaseResponse<String> resp = new BaseResponse<>(HttpStatus.FORBIDDEN.value(),ex.getMessage(),"");
        OutputStream out = res.getOutputStream();
        com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, resp);
        out.flush();
    }
}