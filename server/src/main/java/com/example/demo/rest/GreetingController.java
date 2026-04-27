package com.example.demo.rest;

import org.springframework.web.bind.annotation.RestController;

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
    public boolean signIn(@RequestBody UserData other) {

        boolean res = false;
        System.out.println("acounts: " + users.size());
        for (UserData user : users) {
            if (user.userName().equals(other.userName())) {
                res = user.password().equals(other.password()) ? true : false;
            }
        }
        return res;
    }

    @PostMapping("/register")
    public boolean register(@RequestBody UserData other) {
        System.out.println("acounts: " + users.size());
        for (UserData user : users) {
            if (user.userName().equals(other.userName()))
                return false;
        }
        users.add(other);
        System.out.println("acounts: " + ((UserData) users.toArray()[0]).userName());

        return true;
    }

}
