package com.tecnologia.service.domain;

public class User {
    private final String id;
    private final String username;
    private final String password;
    private final String role;

    public User(String id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getId() {
        return id;
    }
    // Getters y lógica del dominio si es necesario


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
