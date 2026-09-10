package com.aathi.authenticationsystem.controller;

import com.aathi.authenticationsystem.dto.admin.AdminUserPageResponse;
import com.aathi.authenticationsystem.dto.request.RegisterRequest;
import com.aathi.authenticationsystem.dto.response.RegisterResponse;
import com.aathi.authenticationsystem.service.AdminUserService;
import com.aathi.authenticationsystem.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AuthenticationService authenticationService;
    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<AdminUserPageResponse> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ResponseEntity
                .ok(adminUserService.getUsers(page, pageSize));
    }

    @GetMapping("/home")
    @PreAuthorize("hasRole('ADMIN')")
    public String getHomePage(){
        return "Welcome Admin Login Successful!";
    }

    @PostMapping("register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegisterResponse> addAdmin(@Valid @RequestBody RegisterRequest request){
        RegisterResponse response = authenticationService.registerAdmin(request);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @PutMapping("/update/user")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@RequestBody String name){
        return "update user" + name;
    }
}
