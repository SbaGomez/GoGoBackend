package com.tpfinal.gogo.Controller;

import com.tpfinal.gogo.Model.User;
import com.tpfinal.gogo.Service.UserService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("http://localhost:19006")
public class AuthController {
    @Autowired
    private UserService us;

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<Object>> login(@RequestBody Map<String, String> request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String email = request.get("email");
                String password = request.get("clave");
                User user = us.findByEmail(email);
                if (user == null) {
                    return ResponseEntity.status(UNAUTHORIZED).body("Email o contraseña incorrectos");
                }
                if (!isValidPassword(password, user.getClave())) {
                    return ResponseEntity.status(UNAUTHORIZED).body("Email o contraseña incorrectos");
                }
                // If the login is valid, you can create a JWT token and return it in the response
                String token = createJwtToken(user);
                return ResponseEntity.status(OK).body(token);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
            }
        });
    }

/*    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("clave");
            User user = us.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(UNAUTHORIZED).body("Email o contraseña incorrectos");
            }
            if (!isValidPassword(password, user.getClave())) {
                return ResponseEntity.status(UNAUTHORIZED).body("Email o contraseña incorrectos");
            }
            // If the login is valid, you can create a JWT token and return it in the response
            String token = createJwtToken(user);
            return ResponseEntity.status(OK).body(token);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }*/

    private boolean isValidPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    private String createJwtToken(User user) {
        long expirationMillis = 3600000; // Set the expiration time to 1 hour
        String secretKey = System.getenv("JWT_SECRET_KEY");

        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + expirationMillis);

        JwtBuilder builder = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256);
        return builder.compact();
    }
}