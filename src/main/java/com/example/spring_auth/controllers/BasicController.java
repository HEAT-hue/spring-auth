package com.example.spring_auth.controllers;

import com.example.spring_auth.dto.JwtRequestDTO;
import com.example.spring_auth.dto.JwtResponse;
import com.example.spring_auth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {
    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello world");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequestDTO jwtRequestDTO) {
        // Create Authentication token which is used for authentication by auth provider
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(jwtRequestDTO.getUsername(), jwtRequestDTO.getPassword(), null);

        // Authenticate
        authenticationManager.authenticate(authToken);

        String jwtToken = jwtUtil.generateToken(jwtRequestDTO.getUsername());

        return new ResponseEntity<>(new JwtResponse(jwtToken), HttpStatus.OK);
    }
}