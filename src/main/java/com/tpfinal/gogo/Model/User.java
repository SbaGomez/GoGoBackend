package com.tpfinal.gogo.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Data
@Entity
public class User
{

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "apellido")
    private String apellido;
    @Column(name = "dni")
    private String dni;
    @Column(name = "sexo")
    private String sexo;
    @Column(name = "edad")
    private int edad;
    @Column(name = "email")
    private String email;
    @Column(name = "clave")
    private String clave;
}
