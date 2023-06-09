package com.tpfinal.gogo.repository;

import com.tpfinal.gogo.model.ViajeUserAuto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViajeUserAutoRepository extends JpaRepository<ViajeUserAuto, Integer> {

    @Query(value =
            "SELECT v.*, u.nombre, u.apellido, u.edad, u.sexo, a.color, a.marca, a.modelo, a.patente " +
            "FROM viaje v " +
            "INNER JOIN auto a ON a.id = v.auto_id " +
            "INNER JOIN user u ON u.id = v.chofer " +
            "WHERE chofer = :userId", nativeQuery = true)
    List<ViajeUserAuto> findViajesUser(Integer userId);

    @Query(value =
            "SELECT v.*, u.nombre, u.apellido, u.edad, u.sexo, a.color, a.marca, a.modelo, a.patente " +
            "FROM viaje v " +
            "INNER JOIN auto a ON a.id = v.auto_id " +
            "INNER JOIN user u ON u.id = v.chofer " +
            "WHERE FIND_IN_SET(:pasajeroId, v.users) > 0 " +
            "GROUP BY v.id", nativeQuery = true)
    List<ViajeUserAuto> findViajesPasajero(Integer pasajeroId);

    @Query(value =
            "SELECT v.*, u.nombre, u.apellido, u.edad, u.sexo, a.color, a.marca, a.modelo, a.patente " +
            "FROM viaje v " +
            "INNER JOIN auto a ON a.id = v.auto_id " +
            "INNER JOIN user u ON u.id = v.chofer " +
            "WHERE v.id = :viajeId", nativeQuery = true)
    ViajeUserAuto findByIdViaje(Integer viajeId);

    @Query(value =
            "SELECT v.*, u.nombre, u.apellido, u.edad, u.sexo, a.color, a.marca, a.modelo, a.patente " +
            "FROM viaje v " +
            "INNER JOIN auto a ON a.id = v.auto_id " +
            "INNER JOIN user u ON u.id = v.chofer " +
            "WHERE ubicacion_inicio = :ubicacionInicio and ubicacion_destino = :ubicacionDestino", nativeQuery = true)
    List<ViajeUserAuto> findViajesUbicacion(String ubicacionInicio, String ubicacionDestino);

}