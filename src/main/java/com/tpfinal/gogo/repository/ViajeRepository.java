package com.tpfinal.gogo.repository;

import com.tpfinal.gogo.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Integer> {

    @Query(value = "select * from viaje v INNER JOIN auto a ON a.id = v.auto_id WHERE chofer = :id", nativeQuery = true)
    List<Viaje> findViajesUser(Integer id);

    @Query(value = "select * from viaje where ubicacion_inicio = :ubicacionInicio and ubicacion_destino = :ubicacionDestino", nativeQuery = true)
    List<Viaje> findViajesUbicacion(String ubicacionInicio, String ubicacionDestino);

}