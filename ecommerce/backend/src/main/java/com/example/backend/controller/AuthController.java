package com.example.backend.controller;

import com.example.backend.payload.request.ReqUser;
import com.example.backend.payload.request.ReqLogin;
import com.example.backend.security.JwtService;
import com.example.backend.service.authService.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;

    @GetMapping("/get/me")
    public HttpEntity<?> getCurrentUser(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String token = request.getHeader("Authorization");
        return jwtService.getMe(token, response);
    }

    @GetMapping("/users")
    public HttpEntity<?> getUsers(@RequestParam(defaultValue = "") String search) {
        return authService.getUsers(search);
    }

    @PostMapping("/login/public")
    public HttpEntity<?> login(@RequestBody ReqLogin reqLogin, HttpServletResponse response) throws IOException {
        return authService.login(reqLogin, response);
    }

    @PostMapping("/register/user")
    public HttpEntity<?> registerClient(@RequestBody ReqUser reqUser) {
        return authService.registerUser(reqUser);
    }

    @PutMapping("{id}")
    public HttpEntity<?> updateUser(@PathVariable UUID id, @RequestBody ReqUser user) {
        return authService.updateUser(id, user);
    }

    @DeleteMapping("{id}")
    public HttpEntity<?> deleteUser(@PathVariable UUID id) {
        return authService.deleteUser(id);
    }

}
