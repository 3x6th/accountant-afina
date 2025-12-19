package ru.afina.accountant.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/main")
    public String hello() {
        return "Accountant Afina main";
    }
}
