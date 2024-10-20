package com.ing.authentication.dto;

public record RegisterRequest(
        String username,
        String password
) {}