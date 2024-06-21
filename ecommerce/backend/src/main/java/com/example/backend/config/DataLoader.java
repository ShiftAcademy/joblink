package com.example.backend.config;

import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repositoy.RoleRepository;
import com.example.backend.repositoy.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            roles.add(new Role(1, "ROLE_ADMIN"));
            roles.add(new Role(2, "ROLE_CLIENT"));
            roles.add(new Role(3, "ROLE_AGENT"));
            roleRepository.saveAll(roles);

            User admin = new User();
            admin.setId(UUID.randomUUID());
            admin.setPhone("+998901234567");
            admin.setPassword(passwordEncoder.encode("root123"));
            admin.setRoles(roles);
            userRepository.save(admin);
        }


    }
}
