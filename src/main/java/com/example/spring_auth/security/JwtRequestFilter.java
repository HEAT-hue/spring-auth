package com.example.spring_auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtil jwtUtil;

    // Spring Security will call this method during filter chain execution
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        // trying to find Authorization header
        final String authorizationHeader = httpServletRequest.getHeader("Authorization");

        // if Authorization header does not exist, then skip this filter
        // and continue to execute next filter class
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        // Retrieve token from auth header
        final String jwtToken = authorizationHeader.substring(7);
        final String username = jwtUtil.extractUsername(jwtToken);

        if (!jwtUtil.validateToken(jwtToken)) {
            // if token is not valid, then skip this filter
            // and continue to execute next filter class.
            // This means authentication is not successful since token is invalid.
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        // Authorization header exists, token is valid. So, we can authenticate.
        //  If a username is extracted and there is no authentication in the security context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Create an authentication token
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

            // Set details for the authentication token
            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        // end of the method, so go for next filter class
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}