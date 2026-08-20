package com.collegeroom.allocationsystem.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class RoleBasedRedirectHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/login";

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_STUDENT")) {
                redirectUrl = "/student/dashboard";
            } else if (role.equals("ROLE_HOD")) {
                redirectUrl = "/hod/dashboard";
            } else if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
            }
        }

        response.sendRedirect(redirectUrl);
    }
}