package com.tpfinal.gogo.repository;

import com.tpfinal.gogo.model.AutoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoHistoryRepository extends JpaRepository<AutoHistory, Integer> {
}
