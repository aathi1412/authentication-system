package com.aathi.authenticationsystem.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {


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

    @PostMapping("/create/add-user")
    public String addUser(@RequestBody String name){
        return "user added!" + name;
    }

    @PutMapping("/update/user")
    public String updateUser(@RequestBody String name){
        return "update user" + name;
    }
}
