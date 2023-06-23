package com.tpfinal.gogo.repository;

import com.tpfinal.gogo.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Integer> {

    @Query(value = "select user_id from usersxviajes where viaje_id = :id", nativeQuery = true)
    List<Integer> findViajeUser(Integer id);

    @Query(value = "select * from viaje where ubicacion_inicio = :ubicacionInicio and ubicacion_destino = :ubicacionDestino", nativeQuery = true)
    List<Viaje> findViajesUbicacion(String ubicacionInicio, String ubicacionDestino);

}