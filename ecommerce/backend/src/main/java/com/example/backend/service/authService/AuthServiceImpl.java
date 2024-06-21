package com.example.backend.service.authService;

import com.example.backend.entity.Category;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.payload.request.ReqUser;
import com.example.backend.payload.request.ReqLogin;
import com.example.backend.repositoy.CategoryRepository;
import com.example.backend.repositoy.RoleRepository;
import com.example.backend.repositoy.UserRepository;
import com.example.backend.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public HttpEntity<?> getUsers(String search) {
        List<String> roles = new ArrayList<>(List.of("ROLE_AGENT", "ROLE_CLIENT"));
        System.out.println(search);
        if (search.equals("")) {
            return ResponseEntity.ok(userRepository.findUsersByRoles(roles));
        }
        return ResponseEntity.ok(userRepository.findUsersByRolesAndPhone(search));
    }


    @Override
    public HttpEntity<?> login(ReqLogin reqLogin, HttpServletResponse response) throws IOException {
        User user = userRepository.findByPhone(reqLogin.getPhone()).orElseThrow(() -> new NoSuchElementException(""));
        String access_token = null;
        HashMap<String, String> stringMap = new HashMap<>();
        if (user.getRoles().get(0).getName().equals("ROLE_CLIENT") || user.getRoles().get(0).getName().equals("ROLE_AGENT")) {
            if (LocalDateTime.now().isAfter(user.getExpiration_date().toLocalDateTime())) {
                return ResponseEntity.ok().body("kechirasiz siz ushbu login parol bilan autentifikatsiya qilish vaqtingiz tugagan!");
            }
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            reqLogin.getPhone(),
                            reqLogin.getPassword()
                    )
            );

            if (user.getExpiration_date() != null) {
                access_token = jwtService.generateJWT(user, user.getExpiration_date());
                stringMap.put("access_token", access_token);
                stringMap.put("categoryId", String.valueOf(user.getCategory().getId()));
            } else {
                access_token = jwtService.generateJWT(user, null);
                stringMap.put("access_token", access_token);
            }
            return ResponseEntity.ok(stringMap);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            response.setContentType("application/json");
            response.getWriter().write("Wrong phone number or password!");
            response.getWriter().close();
            return null;
        }
    }

    @Override
    public HttpEntity<?> registerUser(ReqUser reqUser) {
        if (LocalDateTime.now().isAfter(reqUser.getExpiration_date().toLocalDateTime())) {
            return ResponseEntity.ok().body("userga berilayotgan vaqt hozirgi vaqtdan keyinda bo'lishi kerak!");
        }
        Category category = categoryRepository.findById(UUID.fromString(reqUser.getCategory_id())).orElseThrow(null);
        List<Role> roles = new ArrayList<>();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setPhone(reqUser.getPhone());
        user.setPassword((passwordEncoder.encode(reqUser.getPassword())));
        user.setCategory(category);
        if (reqUser.getRole_name().equals("client") || reqUser.getRole_name().equals("")) {
            Role role = roleRepository.findById(2).orElseThrow(null);
            roles.add(role);
            user.setRoles(roles);
        } else if (reqUser.getRole_name().equals("agent")) {
            Role role = roleRepository.findById(3).orElseThrow(null);
            roles.add(role);
            user.setRoles(roles);
        }
        user.setExpiration_date(reqUser.getExpiration_date());
        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    @Override
    public HttpEntity<?> updateUser(UUID id, ReqUser user) {
        if (user.getExpiration_date().before(Timestamp.valueOf(LocalDateTime.now()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("o'zgartirilayotgan userning vaqti hozirgi vaqtdan keyinda bo'lishi kerak!");
        }
        User findedUser = userRepository.findById(id).orElseThrow(null);
        Category category = categoryRepository.findById(UUID.fromString(user.getCategory_id())).orElseThrow(null);
        findedUser.setPhone(user.getPhone());
        if (!user.getPassword().equals("")) {
            findedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        findedUser.setCategory(category);
        findedUser.setExpiration_date(user.getExpiration_date());
        return ResponseEntity.ok(userRepository.save(findedUser));
    }

    @Override
    public HttpEntity<?> deleteUser(UUID id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok().body("successful deleted");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("error ");
        }
    }
}
