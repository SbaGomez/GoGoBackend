package com.tpfinal.gogo.controller;

import com.tpfinal.gogo.exceptions.*;
import com.tpfinal.gogo.model.User;
import com.tpfinal.gogo.service.UserService;
import com.tpfinal.gogo.tools.ValidateService;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

import static com.tpfinal.gogo.tools.EmailService.isValidEmailAddress;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("user")
@CrossOrigin("http://localhost:19006")
public class UserController {
    @Autowired
    private UserService us;
    private final BlockingQueue<String> verificationCodeQueue = new LinkedBlockingQueue<>();
    String codigoLocal;


    private record UserResponse(User user, String message) {
    }

    private record UserListResponse(List<User> users, String message) {
    }

    @Async
    @PostMapping("/addUser")
    public CompletableFuture<ResponseEntity<Object>> addUser(@RequestBody Map<String, String> request) {
        return CompletableFuture.supplyAsync(() -> {
            User u = new User();
            int tipoEmail = Integer.parseInt(request.get("tipoEmail"));
            u.setNombre(request.get("nombre"));
            u.setApellido(request.get("apellido"));
            u.setDni(request.get("dni"));
            u.setSexo(request.get("sexo"));
            u.setEdad(Integer.parseInt(request.get("edad")));
            u.setEmail(request.get("email"));
            u.setClave(request.get("clave"));
            List<String> errors = ValidateService.validateUser(u);
            try {
                if (!errors.isEmpty()) {
                    String errorMessage = String.join("\n", errors);
                    throw new BadRequestException(errorMessage);
                }

                String verificationCode = isValidEmailAddress(u.getEmail(), tipoEmail);

                if (verificationCode == null) {
                    return ResponseEntity.status(NOT_FOUND).body("El email no se pudo validar");
                }

                codigoLocal = verificationCode;
                String enteredCode = verificationCodeQueue.take();
                String hashedPassword = BCrypt.hashpw(u.getClave(), BCrypt.gensalt());
                u.setClave(hashedPassword);

                if (enteredCode.equals(verificationCode)) {
                    us.addUser(u);
                    return ResponseEntity.status(OK).body("Usuario registrado");
                }
                return ResponseEntity.status(NOT_FOUND).body("Usuario no registrado");
            } catch (BadRequestException e) {
                return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(BAD_REQUEST).body("Hubo un error al cargar el usuario");
            } catch (Exception e) {
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
            }
        });
    }

    @Async
    @PostMapping("/verificarCodigo")
    public CompletableFuture<ResponseEntity<Object>> verificarCodigo(@RequestBody Map<String, String> request) {
        return CompletableFuture.supplyAsync(() -> {
            String codigo = request.get("codigo");
            try {
                if (codigo != null) {
                    if (codigo.equals(codigoLocal)) {
                        verificationCodeQueue.put(codigo);
                        return ResponseEntity.status(OK).body("Codigo recibido");
                    }
                    return ResponseEntity.status(BAD_REQUEST).body("Código inválido");
                }
                return ResponseEntity.status(NOT_FOUND).body("Código no encontrado");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(BAD_REQUEST).body("Hubo un error al validar el codigo");
            } catch (Exception e) {
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
            }
        });
    }

    @Async
    @PostMapping("/emailExists")
    public CompletableFuture<Boolean> emailExists(@RequestBody Map<String, String> request) {
        return CompletableFuture.supplyAsync(() -> {
            String email = request.get("email");
            User user = us.findByEmail(email);
            if (user != null) {
                return user.getEmail().equals(email);
            }
            return false;
        });
    }

    @Async
    @PostMapping("/dniExists")
    public CompletableFuture<Boolean> dniExists(@RequestBody Map<String, String> request) {
        return CompletableFuture.supplyAsync(() -> {
            String dni = request.get("dni");
            User user = us.findByDni(dni);
            if (user != null) {
                return user.getDni().equals(dni);
            }
            return false;
        });
    }

    @GetMapping("")
    public ResponseEntity<UserListResponse> getAll() {
        try {
            return ResponseEntity.status(OK).body(new UserListResponse(us.getAll(), "Usuarios recuperados con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new UserListResponse(null, "Hubo un error al recuperar los usuarios"));
        }
    }

    @GetMapping("/total")
    public Integer getTotal() {
        return us.getTotal();
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<Object> updateUser(@PathVariable final @NotNull Integer id, @RequestBody final @NotNull User u) {
        try {
            User updatedUser = us.updateUser(id, u);
            if (updatedUser == null) {
                return ResponseEntity.status(NOT_FOUND).body("Usuario " + id + " no encontrado");
            }
            return ResponseEntity.status(OK).body(new UserResponse(updatedUser, "Usuario " + id + " actualizado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable final @NotNull Integer id) {
        try {
            if (us.existsById(id)) {
                us.deleteUser(id);
                return ResponseEntity.status(OK).body("Usuario " + id + " eliminado con éxito");
            }
            return ResponseEntity.status(NOT_FOUND).body("Usuario " + id + " no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable final @NotNull Integer id) {
        try {
            User user = us.getUser(id);
            if (user == null) {
                return ResponseEntity.status(NOT_FOUND).body("Usuario " + id + " no encontrado");
            }
            return ResponseEntity.status(OK).body(new UserResponse(user, "Usuario " + id + " recuperado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar el usuario");
        }
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Object> getUserByDni(@PathVariable final @NotNull String dni) {
        try {
            User user = us.findByDni(dni);
            if (user == null) {
                return ResponseEntity.status(NOT_FOUND).body("Usuario con dni (" + dni + ") no encontrado");
            }
            return ResponseEntity.status(OK).body(new UserResponse(user, "Usuario con dni (" + dni + ") recuperado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar el usuario");
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Object> getUserByEmail(@@PathVariable final @NotNull String email) {
        try {
            User user = us.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(NOT_FOUND).body("Usuario con email (" + email + ") no encontrado");
            }
            return ResponseEntity.status(OK).body(new UserResponse(user, "Usuario con email (" + email + ") recuperado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar el usuario");
        }
    }
}
