package com.tpfinal.gogo.tools;

import com.tpfinal.gogo.model.User;

import java.util.ArrayList;
import java.util.List;

public class ValidateUserService {

    public static List<String> validateUser(User u) {
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
