package com.example.backend.service.authService;

import com.example.backend.entity.User;
import com.example.backend.payload.request.ReqUser;
import com.example.backend.payload.request.ReqLogin;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.util.UUID;

public interface AuthService {

    HttpEntity<?> getUsers(String search);

    HttpEntity<?> login(ReqLogin reqLogin, HttpServletResponse response) throws IOException;

    HttpEntity<?> registerUser(ReqUser reqUser);

    HttpEntity<?> updateUser(UUID id, ReqUser user);

    HttpEntity<?> deleteUser(UUID id);

}
