package com.varunu28.orderservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class HeaderValidationFilter extends OncePerRequestFilter {

    private static final String CUSTOM_GATEWAY_HEADER = "X-From-Gateway";
    private static final String CUSTOM_GATEWAY_VALUE = "true";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (request.getHeader(CUSTOM_GATEWAY_HEADER) == null ||
            !request.getHeader(CUSTOM_GATEWAY_HEADER).equals(CUSTOM_GATEWAY_VALUE)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
