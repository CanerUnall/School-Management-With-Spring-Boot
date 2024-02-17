package com.project.controller;

import com.project.entity.concretes.user.User;
import com.project.payload.request.authentication.LoginRequest;
import com.project.payload.response.authentication.AuthResponse;
import com.project.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login") // http://localhost:8080/auth/login  + POST
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        return authenticationService.authenticateUser(loginRequest);
    }

    /* @GetMapping("/user") // http://localhost:8080/auth/user + GET
    public ResponseEntity<?> findByUsername(HttpServletRequest request){

    }*/
}














