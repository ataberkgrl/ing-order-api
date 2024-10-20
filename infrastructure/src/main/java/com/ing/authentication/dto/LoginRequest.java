package com.ing.authentication.dto;

public record LoginRequest(
        String username,
        String password
) {}