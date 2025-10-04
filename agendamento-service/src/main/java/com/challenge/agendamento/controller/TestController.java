package com.challenge.agendamento.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/auth")
    public Map<String, Object> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();
        result.put("authenticated", auth.isAuthenticated());
        result.put("name", auth.getName());
        result.put("authorities", auth.getAuthorities());
        result.put("principal", auth.getPrincipal().getClass().getSimpleName());
        return result;
    }
}

