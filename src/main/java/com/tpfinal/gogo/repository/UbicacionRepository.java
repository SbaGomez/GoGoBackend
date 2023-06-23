package com.tpfinal.gogo.repository;

import com.tpfinal.gogo.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    boolean existsByNombre(String nombre);

}