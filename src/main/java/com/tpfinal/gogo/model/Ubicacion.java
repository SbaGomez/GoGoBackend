package com.tpfinal.gogo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Data
@Entity
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "nombre", nullable = false)
    private String nombre;
    @JsonIgnore
    @OneToOne(mappedBy = "ubicacionInicio")
    private Viaje viajeInicio;
    @JsonIgnore
    @OneToOne(mappedBy = "ubicacionDestino")
    private Viaje viajeDestino;
}

