package com.tpfinal.gogo.repository;

import com.tpfinal.gogo.model.Auto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoRepository extends JpaRepository<Auto, Integer> {
    Auto findByPatente(String patente);
}
