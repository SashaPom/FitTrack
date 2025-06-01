package com.duikt.fittrack.API;

import com.duikt.fittrack.domain.UserDTO;
import com.duikt.fittrack.domain.auth.LoginDTO;
import com.duikt.fittrack.domain.auth.RegisterDTO;
import com.duikt.fittrack.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody RegisterDTO request) {
        UserDTO user = authService.registerUser(request);
        String token = authService.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful");
        response.put("token", token);
        response.put("user", user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@Valid @RequestBody LoginDTO request) {
        System.out.println("Logging in with email: " + request.getEmail());
        System.out.println("Received password: " + request.getPassword());

        UserDTO user = authService.loginUser(request);
        String token = authService.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("user", user);
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
