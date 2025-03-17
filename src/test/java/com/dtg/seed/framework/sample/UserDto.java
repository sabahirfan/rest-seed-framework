package com.dtg.seed.framework.sample;

import lombok.Data;

/**
 * Sample DTO class for demonstrating the usage of the seed data framework.
 * This class represents a user entity that can be serialized/deserialized from JSON.
 */
@Data
public class UserDto {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
} 