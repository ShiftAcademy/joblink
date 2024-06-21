package com.example.backend.security;

import com.example.backend.dto.CheckUserDTO;
import com.example.backend.entity.User;
import com.example.backend.repositoy.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Key;
import java.sql.Timestamp;
import java.util.Date;

//yangi security
@Component
@RequiredArgsConstructor
public class JwtService {

    private final UserRepository userRepository;

    public String generateJWT(User user, Timestamp expirationDate) {
        return Jwts.builder()
                .signWith(generateSecretKey())
                .setSubject(user.getPhone())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date((expirationDate != null ? expirationDate.getTime() : System.currentTimeMillis() + 60 * 60 * 1000 * 24 * 10))) // bu 10 kunlik
                .compact();
    }

    private Key generateSecretKey() {
        byte[] bytes = "O'zbekistonvatanimmanimgullayashnahuro'zbekistonshiftacademyengzroquvmarkaz".getBytes();
        return Keys.hmacShaKeyFor(bytes);
    }

    public String extractSubjectFromJWT(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(generateSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateToken(String authToken, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        try {
            Jwts.parser().setSigningKey(generateSecretKey()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            // Invalid JWT signature
            response.getWriter().write("Invalid signature");
        } catch (MalformedJwtException e) {
            // Invalid JWT token
            response.getWriter().write("Invalid token");
        } catch (ExpiredJwtException e) {
            // Expired JWT token
            response.getWriter().write("Expired token");
        } catch (UnsupportedJwtException e) {
            // Unsupported JWT token
            response.getWriter().write("Unsupported token");
        } catch (IllegalArgumentException e) {
            // JWT claims string is empty
            response.getWriter().write("token is empty");
        }
        response.getWriter().close();
        return false;
    }

    public HttpEntity<?> getMe(String token, HttpServletResponse response) throws IOException {
        boolean validatedToken = validateToken(token, response);
        if (validatedToken) {
            String phone = extractSubjectFromJWT(token);
            User user = userRepository.findByPhone(phone).orElseThrow(null);
            CheckUserDTO userDTO = new CheckUserDTO(user.getPhone(), user.getRoles(), user.getExpiration_date(), user.getCategory() != null ? user.getCategory().getId() : null);
            return ResponseEntity.ok(userDTO);
        }
        return null;
    }

}
