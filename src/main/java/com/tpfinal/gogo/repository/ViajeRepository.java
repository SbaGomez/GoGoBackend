package com.tpfinal.gogo.repository;

import com.tpfinal.gogo.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Integer> {
    Viaje findByInicio(String inicio);
    Viaje findByDestino(String destino);
}