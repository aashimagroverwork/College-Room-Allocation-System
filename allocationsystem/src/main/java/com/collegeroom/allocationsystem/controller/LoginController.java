package com.collegeroom.allocationsystem.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();
                if (role.equals("ROLE_STUDENT")) {
                    return "redirect:/student/dashboard";
                } else if (role.equals("ROLE_HOD")) {
                    return "redirect:/hod/dashboard";
                } else if (role.equals("ROLE_ADMIN")) {
                    return "redirect:/admin/dashboard";
                }
            }
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboardRedirect(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_STUDENT")) {
                return "redirect:/student/dashboard";
            } else if (role.equals("ROLE_HOD")) {
                return "redirect:/hod/dashboard";
            } else if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}