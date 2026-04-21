package com.example.demo.assets;

public record Auth(String userName, String password) {

    public boolean equals(Auth other) {
        return userName.equals(other.userName()) && password.equals(other.password());
    }

}
