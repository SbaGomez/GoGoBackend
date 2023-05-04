package com.tpfinal.gogo.repository;

import com.tpfinal.gogo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByDni(String dni);
    User findByEmail(String email);
}
