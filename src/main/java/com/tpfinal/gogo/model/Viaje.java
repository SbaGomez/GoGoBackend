package com.tpfinal.gogo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Data
@Entity
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "horario_salida", nullable = false)
    private LocalDate horarioSalida;
    @Column(name = "horario_llegada", nullable = false)
    private LocalDate horarioLlegada;
//    @Column(name = "chat_id", nullable = false)
//    private Chat chat;
    @OneToOne
    @JoinColumn(name = "inicio_id", referencedColumnName = "id", nullable = false)
    private Ubicacion inicio;
    @OneToOne
    @JoinColumn(name = "destino_id", referencedColumnName = "id", nullable = false)
    private Ubicacion destino;

}
