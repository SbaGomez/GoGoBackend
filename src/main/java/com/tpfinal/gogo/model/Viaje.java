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
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "horario_salida", nullable = false)
    private LocalDateTime horarioSalida;
    @Column(nullable = false)
    private LocalDateTime expirationDate;
    @Column(name = "turno", nullable = false)
    private String turno;
    @Column(nullable = false)
    private String ubicacionInicio;
    @Column(nullable = false)
    private String ubicacionDestino;
    @Column(nullable = false)
    private String detalle;
    @Column(nullable = false)
    private int chofer;
    @Column(nullable = false)
    private int autoId;
    @Column(name = "capacidad", nullable = false)
    private int maxCapacidad;

    private String users;
}
