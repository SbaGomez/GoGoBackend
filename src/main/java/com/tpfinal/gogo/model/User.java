package com.tpfinal.gogo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "nombre", nullable = false)
    private String nombre;
    @Column(name = "apellido", nullable = false)
    private String apellido;
    @Column(name = "dni", nullable = false)
    private String dni;
    @Column(name = "sexo", nullable = false)
    private String sexo;
    @Column(name = "edad", nullable = false)
    private int edad;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "clave", nullable = false)
    private String clave;
    @OneToOne
    @JoinColumn(name = "auto_id")
    private Auto auto;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<AutoHistory> autoHistoryList;
    @ManyToMany
    @JoinTable(
            name="userxviaje",
            joinColumns=
            @JoinColumn(name="user_id"),
            inverseJoinColumns=
            @JoinColumn(name="viaje_id"))
    private List<Viaje> viajes;
}
