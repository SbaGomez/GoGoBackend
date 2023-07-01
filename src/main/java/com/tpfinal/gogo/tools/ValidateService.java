package com.tpfinal.gogo.tools;

import com.tpfinal.gogo.model.Auto;
import com.tpfinal.gogo.model.User;
import com.tpfinal.gogo.model.Viaje;

import java.util.ArrayList;
import java.util.List;

public class ValidateService {

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

    public static List<String> validateAuto(Auto a) {
        List<String> errors = new ArrayList<>();
        if (a.getPatente() == null || a.getPatente().isEmpty()) {
            errors.add("La patente es requerida");
        }
        if (a.getColor() == null || a.getColor().isEmpty()) {
            errors.add("El color es requerido");
        }
        if (a.getModelo() == null || a.getModelo().isEmpty()) {
            errors.add("El modelo es requerido");
        }
        if (a.getMarca() == null || a.getMarca().isEmpty()) {
            errors.add("La marca es requerida");
        }
        return errors;
    }

    public static List<String> validateViaje(Viaje v) {
        List<String> errors = new ArrayList<>();
        if (v.getHorarioSalida() == null) {
            errors.add("El horario de salida es requerido");
        }
        if (v.getUbicacionInicio() == null) {
            errors.add("El inicio es requerido");
        }
        if (v.getUbicacionDestino() == null) {
            errors.add("El destino es requerido");
        }
        if (v.getDetalle() == null) {
            errors.add("El detalle es requerido");
        }
        if (v.getTurno() == null) {
            errors.add("El turno es requerido");
        }
        if (v.getMaxCapacidad() == 0) {
            errors.add("La capacidad máxima es requerida");
        } else if (v.getMaxCapacidad() < 1) {
            errors.add("La capacidad máxima debe ser mayor o igual a 1");
        }
        if (v.getAutoId() == 0) {
            errors.add("El id del auto es requerido");
        } else if (v.getAutoId() < 1) {
            errors.add("El id del auto debe ser mayor o igual a 1");
        }
        if (v.getChofer() == 0) {
            errors.add("El id del chofer es requerido");
        } else if (v.getChofer() < 1) {
            errors.add("El id del chofer debe ser mayor o igual a 1");
        }
        return errors;
    }
}
