package com.example.demo.assets;

public record UserData(boolean inSystem, String userName, String password, int wins, int losses) {
    public UserData(boolean inSystem) {
        this(inSystem, null, null, 0, 0);
    }
}
