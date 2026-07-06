package com.aathi.authenticationsystem.controller;

import com.aathi.authenticationsystem.dto.request.RegisterRequest;
import com.aathi.authenticationsystem.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthenticationService authenticationService;

    @GetMapping("/home")
    @PreAuthorize("hasRole('ADMIN')")
    public String getHomePage(){
        return "Welcome Login Successfull!";
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String getDashboard(){
        return "Dashboard!";
    }

    @GetMapping("/about")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAbout(){
        return "About!";
    }

    @GetMapping("/contact")
    @PreAuthorize("hasRole('ADMIN')")
    public String getContact(){
        return "Contact!";
    }

    @PostMapping("/create/add-user")
    @PreAuthorize("hasRole('ADMIN')")
    public String addUser(@RequestBody String name){
        return "user added!" + name;
    }

    @PostMapping("/create/add-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String addAdmin(@RequestBody RegisterRequest request){
        authenticationService.register(request);
        return "Admin added!";
    }

    @PutMapping("/update/user")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@RequestBody String name){
        return "update user" + name;
    }
}
