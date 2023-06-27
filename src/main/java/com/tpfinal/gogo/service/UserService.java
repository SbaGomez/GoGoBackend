package com.tpfinal.gogo.service;

import com.tpfinal.gogo.model.User;
import com.tpfinal.gogo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository ur;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.ur = userRepository;
    }

    public void addUser(User user) {
        ur.save(user);
    }

    public List<User> getAll() {
        return ur.findAll();
    }

    public Integer getTotal() {
        return ur.findAll().size();
    }

    public User updateUser(Integer id, User user) {
        User u = ur.findById(id).orElse(null);
        if (u != null) {
            if (user.getNombre() != null) {
                u.setNombre(user.getNombre());
            }
            if (user.getApellido() != null) {
                u.setApellido(user.getApellido());
            }
            if (user.getDni() != null) {
                u.setDni(user.getDni());
            }
            if (user.getSexo() != null) {
                u.setSexo(user.getSexo());
            }
            if (user.getEdad() != 0) {
                u.setEdad(user.getEdad());
            }
            if (user.getEmail() != null) {
                u.setEmail(user.getEmail());
            }
            if (user.getClave() != null) {
                u.setClave(user.getClave());
            }
            if (user.getAuto() != null) {
                u.setAuto(user.getAuto());
            }
            ur.save(u);
        }
        return u;
    }

    public boolean existsById(Integer id) {
        return ur.existsById(id);
    }

    public void deleteUser(Integer id) {
        ur.deleteById(id);
    }

    public User getUser(Integer id) {
        return ur.findById(id).orElse(null);
    }

    public User findByDni(String dni) {
        return ur.findByDni(dni);
    }

    public User findByEmail(String email) {
        return ur.findByEmail(email);
    }
}
