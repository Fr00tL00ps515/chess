package com.example.demo.rest;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.assets.AuthData;
import com.example.demo.assets.UserData;

import ch.qos.logback.core.joran.spi.HttpUtil.RequestMethod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "*")
@RestController
public class GreetingController {

    Set<UserData> users = new HashSet<>();

    @PostMapping("/sign_in")
    public UserData signIn(@RequestBody AuthData other) {

        UserData res = new UserData(false);
        System.out.println("acounts: " + users.size());
        for (UserData user : users) {
            if (user.userName().equals(other.userName()) && user.password().equals(other.password())) {
                System.out.println("Sign in sucessfull");
                res = user;
            }
        }
        return res;
    }

    @PostMapping("/register")
    public boolean register(@RequestBody AuthData other) {
        System.out.println("acounts: " + users.size());
        for (UserData user : users) {
            if (user.userName().equals(other.userName()))
                return false;
        }
        users.add(new UserData(true, other.userName(), other.password(), 0, 0));
        System.out.println("acounts: " + ((UserData) users.toArray()[0]).userName());

        return true;
    }

}
