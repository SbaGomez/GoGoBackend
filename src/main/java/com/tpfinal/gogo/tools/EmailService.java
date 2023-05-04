package com.tpfinal.gogo.tools;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import java.io.IOException;

import static com.tpfinal.gogo.tools.VerificationCode.generateVerificationCode;

public class EmailService {
    public static String isValidEmailAddress(String email, int tipoEmail) {
        String senderEmail = System.getenv("SENDER_EMAIL");
        String verificationCode = generateVerificationCode();
        try {
            Email from = new Email(senderEmail);
            Email to = new Email(email);
            String subject;
            Content content;
            if (tipoEmail == 0) {
                subject = "Verificación";
                content = new Content("text/html", "Verifique su dirección de correo electrónico: " + verificationCode);
            } else if (tipoEmail == 1) {
                subject = "Recupero contraseña";
                content = new Content("text/html", "Recupere su contraseña ingresando el siguiente código en la app: " + verificationCode);
            } else {
                throw new IllegalArgumentException("tipoEmail must be either 0 or 1");
            }

            Mail mail = new Mail(from, subject, to, content);

            if (tipoEmail == 0) {
                //verificación
                Personalization personalization = new Personalization();
                personalization.addTo(to);
                personalization.addDynamicTemplateData("code", verificationCode);
                mail.addPersonalization(personalization);
                mail.setTemplateId("d-24471fdde8f84f92ab5033a5c55009d9");
                mail.getPersonalization().get(0).addDynamicTemplateData("code", verificationCode);
            } else {
                //recupero
                Personalization personalization = new Personalization();
                personalization.addTo(to);
                personalization.addDynamicTemplateData("code", verificationCode);
                mail.addPersonalization(personalization);
                mail.setTemplateId("567845678");
                mail.getPersonalization().get(0).addDynamicTemplateData("code", verificationCode);
            }
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
