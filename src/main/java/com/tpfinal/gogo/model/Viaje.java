package com.tpfinal.gogo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    @Column(name = "turno", nullable = false)
    private String turno;
    @OneToOne
    @JoinColumn(name = "inicio_id", referencedColumnName = "id", nullable = false)
    private Ubicacion ubicacionInicio;
    @OneToOne
    @JoinColumn(name = "destino_id", referencedColumnName = "id", nullable = false)
    private Ubicacion ubicacionDestino;
    @JsonIgnore
    @ManyToMany(mappedBy = "viajes")
    private List<User> users;

}
