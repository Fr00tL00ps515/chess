package com.example.demo.rest;

import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.core.joran.spi.HttpUtil.RequestMethod;

import java.util.Arrays;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class GreetingController {

    private static String[][] field = {
            { "5", "4", "3", "2", "1", "3", "4", "5" },
            { "6", "6", "6", "6", "6", "6", "6", "6" },
            { " ", " ", " ", " ", " ", " ", " ", " " },
            { " ", " ", " ", " ", " ", " ", " ", " " },
            { " ", " ", " ", " ", " ", " ", " ", " " },
            { " ", " ", " ", " ", " ", " ", " ", " " },
            { "6", "6", "6", "6", "6", "6", "6", "6" },
            { "5", "4", "3", "2", "1", "3", "4", "5" } };

    private static void printField() {
        for (String[] i : field) {
            System.out.println(Arrays.toString(i));
        }
    }

    @PostMapping("/greeting")
    public String greeting(@RequestBody String[][] field) {
        this.field = field;
        System.out.println("New field: ");
        printField();
        return "asdadssads";
    }

    @GetMapping("/")
    public String anotherGreeting() {
        return "Hello world";
    }

}
