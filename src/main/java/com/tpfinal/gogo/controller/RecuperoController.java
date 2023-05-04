package com.tpfinal.gogo.controller;

import com.tpfinal.gogo.exceptions.BadRequestException;
import com.tpfinal.gogo.model.User;
import com.tpfinal.gogo.service.UserService;

import com.tpfinal.gogo.tools.EmailService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/recupero")
@CrossOrigin("http://localhost:19006")
public class RecuperoController {
    @Autowired
    private UserService us;
    private String code;
    private String emailLocal;

    @Async
    @PostMapping("/validarMail")
    public CompletableFuture<ResponseEntity<Object>> validarMail(@RequestBody Map<String, String> request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String email = request.get("email");
                int tipoEmail = Integer.parseInt(request.get("tipoEmail"));
                User user = us.findByEmail(email);
                if (user == null) {
                    return ResponseEntity.status(NOT_FOUND).body("Email no registrado");
                }
                if (email == null || !email.matches(".+@uade\\.edu\\.ar")) {
                    throw new BadRequestException("El email debe ser del dominio @uade.edu.ar y tener una parte local no vacía");
                }
                String verificationCode = EmailService.isValidEmailAddress(email, tipoEmail);
                if (verificationCode == null) {
                    return ResponseEntity.status(NOT_FOUND).body("El email no se pudo validar");
                }
                code = verificationCode;
                emailLocal = email;
                return ResponseEntity.status(OK).body("Email enviado");
            } catch (Exception e) {
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
            }
        });
    }

    @Async
    @PostMapping("/updateClave")
    public CompletableFuture<ResponseEntity<Object>> updateClave(@RequestBody Map<String, String> request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String codigo = request.get("codigo");
                String clave = request.get("clave");
                if (codigo.equals(code)) {
                    User user = us.findByEmail(emailLocal);
                    String hashedPassword = BCrypt.hashpw(clave, BCrypt.gensalt());
                    int id = user.getId();
                    user.setClave(hashedPassword);
                    us.updateUser(id, user);
                    return ResponseEntity.status(OK).body("Clave actualizada correctamente");
                }
                return ResponseEntity.status(BAD_REQUEST).body("Código inválido");
            } catch (Exception e) {
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
            }
        });
    }
}

