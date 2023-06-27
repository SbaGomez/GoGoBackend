package com.tpfinal.gogo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<Integer> users;

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
