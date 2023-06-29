package com.tpfinal.gogo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Data
@Entity
public class ViajeUserAuto {

    //viaje
    @Id
    private int id;
    private LocalDateTime horarioSalida;
    private String turno;
    private String ubicacionInicio;
    private String ubicacionDestino;
    private int chofer;
    private int autoId;
    private int capacidad;
    private String users;

    //user
    private String nombre;
    private String apellido;
    private String sexo;
    private int edad;

    //auto
    private String patente;
    private String color;
    private String modelo;
    private String marca;

}
