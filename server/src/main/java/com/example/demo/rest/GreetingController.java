package com.example.demo.rest;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.assets.Auth;

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

    Set<Auth> auths = new HashSet<>();

    @PostMapping("/sign_in")
    public boolean signIn(@RequestBody Auth other) {
        System.out.println("Got request");
        for (Auth auth : auths) {
            if (auth.userName().equals(other.userName())) {
                return auth.password().equals(other.password()) ? true : false;
            }
        }
        return false;
    }

    @PostMapping("/register")
    public boolean register(@RequestBody Auth other) {
        for (Auth auth : auths) {
            if (auth.userName().equals(other.userName()))
                return false;
        }
        auths.add(other);
        return true;
    }

}
