package com.example.warehouse.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/public")
    public Map<String, Object> publicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a public endpoint");
        response.put("timestamp", System.currentTimeMillis());

        // Also show auth details if available
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            response.put("authenticated", auth.isAuthenticated());
            response.put("principal", auth.getName());
            if (auth.getAuthorities() != null) {
                response.put("authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));
            }
        }

        return response;
    }

    @GetMapping("/client")
    public Map<String, Object> clientEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a CLIENT-only endpoint");

        return getStringObjectMap(response);
    }

    @GetMapping("/manager")
    public Map<String, Object> managerEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a MANAGER-only endpoint");

        return getStringObjectMap(response);
    }

    @GetMapping("/admin")
    public Map<String, Object> adminEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is an ADMIN-only endpoint");

        return getStringObjectMap(response);
    }

    private Map<String, Object> getStringObjectMap(Map<String, Object> response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        response.put("authenticated", auth.isAuthenticated());
        response.put("principal", auth.getName());
        response.put("authorities", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return response;
    }
}