
package com.aathi.authenticationsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/home")
    public String getHomePage(){
        return "Welcome Login Successfull!";
    }

    @GetMapping("/dashboard")
    public String getDashboard(){
        return "Dashboard!";
    }

    @GetMapping("/about")
    public String getAbout(){
        return "About!";
    }

    @GetMapping("/contact")
    public String getContact(){
        return "Contact!";
    }
}