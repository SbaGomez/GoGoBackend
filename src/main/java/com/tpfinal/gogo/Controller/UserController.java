package com.tpfinal.gogo.Controller;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.tpfinal.gogo.Exceptions.*;
import com.tpfinal.gogo.Model.User;
import com.tpfinal.gogo.Service.UserService;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

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

    @Async
    @PostMapping("/addUser")
    public CompletableFuture<ResponseEntity<Object>> addUser(@RequestBody final @NotNull User u) {
        return CompletableFuture.supplyAsync(() -> {
            String verificationCode = isValidEmailAddress(u.getEmail());
            List<String> errors = validateUser(u);
            try {
                if (!errors.isEmpty()) {
                    String errorMessage = String.join("\n", errors);
                    throw new BadRequestException(errorMessage);
                }
                if (verificationCode == null) {
                    return ResponseEntity.status(NOT_FOUND).body("El email no se pudo validar");
                }
                String hashedPassword = BCrypt.hashpw(u.getClave(), BCrypt.gensalt());
                u.setClave(hashedPassword);
                return ResponseEntity.status(OK).body(new UserResponse(us.addUser(u), verificationCode));
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
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar el usuario");
        }
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Object> getUserByDni(@PathVariable String dni) {
        try {
            User user = us.findByDni(dni);
            if (user == null) {
                return ResponseEntity.status(NOT_FOUND).body("Usuario con " + dni + " no encontrado");
            }
            return ResponseEntity.status(OK).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar el usuario");
        }
    }

    private static String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return Integer.toString(code);
    }

    public static String isValidEmailAddress(String email) {
        String senderEmail = System.getenv("SENDER_EMAIL");
        String verificationCode = generateVerificationCode();
        try {
            Email from = new Email(senderEmail);
            Email to = new Email(email);
            String subject = "Verificación";
            Content content = new Content("text/html", "Verifique su dirección de correo electrónico: " + verificationCode);

            Mail mail = new Mail(from, subject, to, content);

            Personalization personalization = new Personalization();
            personalization.addTo(to);
            personalization.addDynamicTemplateData("code", verificationCode);
            mail.addPersonalization(personalization);
            mail.setTemplateId("d-24471fdde8f84f92ab5033a5c55009d9");
            mail.getPersonalization().get(0).addDynamicTemplateData("code", verificationCode);


            SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println(response.getStatusCode());
            System.out.println(response.getHeaders());
            System.out.println(response.getBody());
            int statusCode = response.getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                return verificationCode;
            } else {
                System.out.println(response.getBody());
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
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
        if (u.getEmail() == null || !u.getEmail().matches(".+@uade\\.edu\\.ar")) {
            errors.add("El email debe ser del dominio @uade.edu.ar y tener una parte local no vacía");
        }
        if (u.getClave() == null || u.getClave().isEmpty()) {
            errors.add("La clave es requerida");
        }
        return errors;
    }
}
