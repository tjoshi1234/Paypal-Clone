package com.paypal.user_service.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTRequestFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // ======= 1. Handle CORS =======
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return; // Stop filter for preflight
        }

        // ======= 2. Extract Authorization Header =======
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            try {
                userName = jwtUtil.extractUsername(token);
            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                // Token expired → return custom response
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT Token Expired");
                return;
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT Token");
                return;
            }
        }

        // ======= 3. If user is valid & not authenticated =======
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtUtil.validateToken(token, userName)) {

                // Extract role from token
                String role = jwtUtil.extractRole(token);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userName,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Authenticate user
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // ======= 4. Continue Filter Chain =======
        filterChain.doFilter(request, response);
    }
}
