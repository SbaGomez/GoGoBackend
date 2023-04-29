package com.tpfinal.gogo.Controller;

import com.tpfinal.gogo.Exceptions.*;
import com.tpfinal.gogo.Model.User;
import com.tpfinal.gogo.Service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("user")
@CrossOrigin("http://localhost:19006")
public class UserController {
    @Autowired
    private UserService us;

    private record UserResponse(User user, String message) {
    }

    private record UserListResponse(List<User> users, String message) {
    }

    private List<String> validateUser(User u) {
        List<String> errors = new ArrayList<>();
        if (u.getNombre() == null || u.getNombre().isEmpty()) {
            errors.add("El nombre es requerido");
        }
        if (u.getApellido() == null || u.getApellido().isEmpty()) {
            errors.add("El apellido es requerido");
        }
        if (u.getDni() == null || u.getDni().isEmpty()) {
            errors.add("El dni es requerido");
        }
        if (u.getSexo() == null || u.getSexo().isEmpty()) {
            errors.add("El sexo es requerido");
        }
        if (u.getEdad() == 0) {
            errors.add("La edad es requerida");
        }
        if (u.getEmail() == null || u.getEmail().isEmpty()) {
            errors.add("El email es requerido");
        }
        if (u.getClave() == null || u.getClave().isEmpty()) {
            errors.add("La clave es requerida");
        }
        return errors;
    }

    @PostMapping("/addUser")
    public ResponseEntity<Object> addUser(@RequestBody final @NotNull User u) {
        List<String> errors = validateUser(u);
        try {
            if (!errors.isEmpty()) {
                String errorMessage = String.join("\n", errors);
                throw new BadRequestException(errorMessage);
            }
            return ResponseEntity.status(OK).body(new UserResponse(us.addUser(u), "Usuario cargado con éxito"));
        } catch (BadRequestException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(BAD_REQUEST).body("Hubo un error al cargar el usuario");
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
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
        return us.deleteUser(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable final @NotNull Integer id) {
        try {
            User user = us.getUser(id);
            if (user == null) {
                return ResponseEntity.status(NOT_FOUND).body("Usuario " + id + " no encontrado");
            }
            return ResponseEntity.status(OK).body(new UserResponse(us.getUser(id), "Usuario " + id + " recuperado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Object> getUserByDni(@PathVariable String dni) {
        User user = us.findByDni(dni);
        if (user == null) {
            return ResponseEntity.status(NOT_FOUND).body("Usuario con " + dni + " no encontrado");
        }
        return ResponseEntity.status(OK).body(user);
    }
}
