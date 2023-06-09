package com.tpfinal.gogo.service;

import com.tpfinal.gogo.model.*;
import com.tpfinal.gogo.repository.UbicacionRepository;
import com.tpfinal.gogo.repository.ViajeRepository;
import com.tpfinal.gogo.repository.ViajeUserAutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViajeService {
    private final ViajeRepository vr;
    private final ViajeUserAutoRepository vuar;
    private final UbicacionRepository ur;

    @Autowired
    public ViajeService(ViajeRepository viajeRepository, ViajeUserAutoRepository viajeUserAutoRepository, UbicacionRepository ubicacionRepository) {
        this.vr = viajeRepository;
        this.vuar = viajeUserAutoRepository;
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
            if ((viaje.getUsers()) != null) {
                v.setUsers(viaje.getUsers());
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

    public ViajeUserAuto getViajeUserAuto(Integer id) {
        return vuar.findByIdViaje(id);
    }

    public boolean existsByNombre(String nombre) {
        return ur.existsByNombre(nombre);
    }

    public List<ViajeUserAuto> findByUbicacion(String ubicacionInicio, String ubicacionDestino) {
        return vuar.findViajesUbicacion(ubicacionInicio, ubicacionDestino);
    }

    public List<ViajeUserAuto> findMisViajesById(Integer userId) {
        return vuar.findViajesUser(userId);
    }

    public List<ViajeUserAuto> findMisViajesByIdPasajero(Integer pasajeroId) {
        return vuar.findViajesPasajero(pasajeroId);
    }

    public void joinLeaveViaje(Integer id, Viaje viaje) {
        Viaje v = vr.findById(id).orElse(null);
        if (v != null) {
            if ((viaje.getUsers()) != null) {
                v.setUsers(viaje.getUsers());
            }
            vr.save(v);
        }
    }

    public List<Viaje> getViajesByAutoId(int autoId) {
        return vr.findByAutoId(autoId);
    }

}
