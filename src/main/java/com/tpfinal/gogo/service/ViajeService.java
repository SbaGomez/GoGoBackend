package com.tpfinal.gogo.service;

import com.tpfinal.gogo.model.*;
import com.tpfinal.gogo.repository.UbicacionRepository;
import com.tpfinal.gogo.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViajeService {
    private final ViajeRepository vr;
    private final UbicacionRepository ur;

    @Autowired
    public ViajeService(ViajeRepository viajeRepository, UbicacionRepository ubicacionRepository) {
        this.vr = viajeRepository;
        this.ur = ubicacionRepository;
    }

    public void addViaje(Viaje v) {
        vr.save(v);
    }

    public List<Viaje> getAll() {
        return vr.findAll();
    }

    public Integer getTotal() {
        return vr.findAll().size();
    }

    public Viaje updateViaje(Integer id, Viaje viaje) {
        Viaje v = vr.findById(id).orElse(null);
        if (v != null) {
            if ((viaje.getHorarioSalida()) != null) {
                v.setHorarioSalida(viaje.getHorarioSalida());
            }
           if ((viaje.getUbicacionInicio()) != null) {
                v.setUbicacionInicio(viaje.getUbicacionInicio());
            }
            if ((viaje.getUbicacionDestino()) != null) {
                v.setUbicacionDestino(viaje.getUbicacionDestino());
            }
            vr.save(v);
        }
        return v;
    }

    public boolean existsById(Integer id) {
        return vr.existsById(id);
    }

    public void deleteViaje(Integer id) {
        vr.deleteById(id);
    }

    public Viaje getViaje(Integer id) {
        return vr.findById(id).orElse(null);
    }

    public Ubicacion findByUbicacionInicio(Ubicacion inicio) {
        return ur.findByUbicacionInicio(inicio.getNombre());
    }

    public Ubicacion findByUbicacionDestino(Ubicacion destino) {
        return ur.findByUbicacionDestino(destino.getNombre());
    }

}
