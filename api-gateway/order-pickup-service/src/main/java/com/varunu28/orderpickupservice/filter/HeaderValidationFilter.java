package com.varunu28.orderpickupservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class HeaderValidationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "X-Authorized-By";
    private static final String AUTHORIZATION_VALUE = "Auth-Service";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (request.getHeader(AUTHORIZATION_HEADER) == null || !request.getHeader(AUTHORIZATION_HEADER).equals(AUTHORIZATION_VALUE)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
