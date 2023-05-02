package com.tpfinal.gogo.Controller;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.tpfinal.gogo.Exceptions.BadRequestException;
import com.tpfinal.gogo.Model.User;
import com.tpfinal.gogo.Service.UserService;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.tpfinal.gogo.tools.VerificationCode.generateVerificationCode;
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
                String verificationCode = isValidEmailRecupero(email);
                if (email == null || !email.matches(".+@uade\\.edu\\.ar")) {
                    throw new BadRequestException("El email debe ser del dominio @uade.edu.ar y tener una parte local no vacía");
                }
                if (verificationCode == null) {
                    return ResponseEntity.status(NOT_FOUND).body("El email no se pudo validar");
                }
                code = verificationCode;
                emailLocal = email;
                return ResponseEntity.status(OK).body(new UserController.UserResponse(null, verificationCode));
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


    private String isValidEmailRecupero(String email) {
        String senderEmail = System.getenv("SENDER_EMAIL");
        String verificationCode = generateVerificationCode();
        try {
            Email from = new Email(senderEmail);
            Email to = new Email(email);
            String subject = "Recupero contraseña";
            Content content = new Content("text/html", "Recupere su contraseña ingresando el siguiente código en la app: " + verificationCode);

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
}
