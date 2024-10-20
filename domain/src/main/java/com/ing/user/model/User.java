package com.ing.user.model;

import lombok.Value;

@Value
public class User {
    String id;
    String username;
    String password;
    UserType userType;

    public enum UserType {
        CUSTOMER, ADMIN
    }
}