package com.tpfinal.gogo.Service;

import com.tpfinal.gogo.Model.User;
import com.tpfinal.gogo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class UserService
{
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public ResponseEntity<User> addUser(User user)
    {
        try
        {
            // Validar el objeto de entrada
            if (user == null)
            {
                return ResponseEntity.badRequest().build();
            }

            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(this.findByDni(savedUser.getDni()));
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public User findByDni(String dni)
    {
        return (User) Arrays.stream(userRepository.findAll().toArray()).filter(usr -> ((User) usr).getDni().equals(dni)).findFirst().orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Usuario no encontrado"));
    }

    public User getUser(Integer id)
    {
        return userRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "El usuario con el identificador: " + id + " no encontrado."));
    }

    public ResponseEntity<?> deleteUser(Integer id)
    {
        try
        {
            userRepository.deleteById(id);
            return ResponseEntity.status(OK).build();
        }
        catch (Exception e)
        {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }

    }

    public ResponseEntity<User> updateUser(Integer id, User user)
    {
        try{
            User usr = userRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "El usuario con el identificador: " + id + " no encontrado."));
            usr.setEdad(user.getEdad());
            usr.setSexo(user.getSexo());
            usr.setDni(user.getDni());
            usr.setApellido(user.getApellido());
            usr.setNombre(user.getNombre());
            User savedPerfil = userRepository.save(usr);
            return ResponseEntity.ok(this.findByDni(savedPerfil.getDni()));
        }
        catch (Exception e)
        {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }
}
