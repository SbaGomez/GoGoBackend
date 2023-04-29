package com.tpfinal.gogo.Service;

import com.tpfinal.gogo.Exceptions.*;
import com.tpfinal.gogo.Model.User;
import com.tpfinal.gogo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
public class UserService {
    private final UserRepository ur;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.ur = userRepository;
    }

    public User addUser(User user) {
        return ur.save(user);
    }

    public List<User> getAll() {
        return ur.findAll();
    }

    public Integer getTotal() {
        try {
            return ur.findAll().size();
        } catch (Exception e) {
            throw new InternalServerException("Hubo un error al recuperar el total de usuarios");
        }
    }

    public User updateUser(Integer id, User user) {
        User u = ur.findById(id).orElse(null);
        if (u != null) {
            if ((user.getNombre()) != null) {
                u.setNombre(user.getNombre());
            }
            if ((user.getApellido()) != null) {
                u.setApellido(user.getApellido());
            }
            if ((user.getDni()) != null) {
                u.setDni(user.getDni());
            }
            if ((user.getSexo()) != null) {
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
            ur.save(u);
        }
        return u;
    }

    public ResponseEntity<String> deleteUser(Integer id) {
        if (ur.existsById(id)) {
            try {
                ur.deleteById(id);
                return ResponseEntity.status(OK).body("Usuario " + id + " eliminado con éxito");
            } catch (Exception e) {
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
            }
        }
        return ResponseEntity.status(NOT_FOUND).body("Usuario " + id + " no encontrado");
    }

    public User getUser(Integer id) {
        return ur.findById(id).orElse(null);
    }

    public User findByDni(String dni) {
        return ur.findByDni(dni);
    }
}
