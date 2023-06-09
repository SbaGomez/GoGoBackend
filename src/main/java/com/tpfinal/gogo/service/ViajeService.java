package com.tpfinal.gogo.service;

import com.tpfinal.gogo.model.*;
import com.tpfinal.gogo.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViajeService {
    private final ViajeRepository vr;

    @Autowired
    public ViajeService(ViajeRepository viajeRepository) {
        this.vr = viajeRepository;
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
            if ((viaje.getHorarioLlegada()) != null) {
                v.setHorarioLlegada(viaje.getHorarioLlegada());
            }
/*            if ((viaje.getInicio()) != null) {
                v.setInicio(viaje.getInicio());
            }
            if ((viaje.getDestino()) != null) {
                v.setDestino(viaje.getDestino());
            }*/
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

/*    public Viaje findByUbicacionInicio(String inicio) {
        return vr.findByInicio(inicio);
    }

    public Viaje findByUbicacionDestino(String destino) {
        return vr.findByDestino(destino);
    }*/
}
