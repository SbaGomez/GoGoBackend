package com.tpfinal.gogo.service;

import com.tpfinal.gogo.model.Auto;
import com.tpfinal.gogo.repository.AutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutoService {
    private final AutoRepository ar;

    @Autowired
    public AutoService(AutoRepository autoRepository) {
        this.ar = autoRepository;
    }

    public void addAuto(Auto a) {
        ar.save(a);
    }

    public List<Auto> getAll() {
        return ar.findAll();
    }

    public Integer getTotal() {
        return ar.findAll().size();
    }

    public Auto updateAuto(Integer id, Auto auto) {
        Auto a = ar.findById(id).orElse(null);
        if (a != null) {
            if ((auto.getPatente()) != null) {
                a.setPatente(auto.getPatente());
            }
            if ((auto.getColor()) != null) {
                a.setColor(auto.getColor());
            }
            if ((auto.getModelo()) != null) {
                a.setModelo(auto.getModelo());
            }
            if ((auto.getMarca()) != null) {
                a.setMarca(auto.getMarca());
            }
            ar.save(a);
        }
        return a;
    }

    public boolean existsById(Integer id) {
        return ar.existsById(id);
    }

    public void deleteAuto(Integer id) {
        ar.deleteById(id);
    }

    public Auto getAuto(Integer id) {
        return ar.findById(id).orElse(null);
    }

    public Auto findByPatente(String dni) {
        return ar.findByPatente(dni);
    }
}
