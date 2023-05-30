package com.tpfinal.gogo.controller;

import com.tpfinal.gogo.exceptions.BadRequestException;
import com.tpfinal.gogo.model.Viaje;
import com.tpfinal.gogo.model.Ubicacion;
import com.tpfinal.gogo.service.ViajeService;
import com.tpfinal.gogo.tools.ValidateService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("viaje")
@CrossOrigin("http://localhost:19006")
public class ViajeController {
    @Autowired
    private ViajeService vs;

    private record ViajeListResponse(List<Viaje> viajes, String message) {
    }

    private record ViajeResponse(Viaje viaje, String message) {
    }

    @Async
    @PostMapping("/addViaje")
    public CompletableFuture<ResponseEntity<Object>> addViaje(@RequestBody Map<String, String> request) {
        return CompletableFuture.supplyAsync(() -> {
            Viaje v = new Viaje();
            v.setHorarioSalida(LocalDate.parse(request.get("horarioSalida")));
            v.setHorarioLlegada(LocalDate.parse(request.get("horarioLlegada")));

            Ubicacion inicio = new Ubicacion();
            inicio.setNombre(request.get("inicio"));
            v.setInicio(inicio);

            Ubicacion destino = new Ubicacion();
            destino.setNombre(request.get("destino"));
            v.setDestino(destino);

            List<String> errors = ValidateService.validateViaje(v);
            try {
                if (!errors.isEmpty()) {
                    String errorMessage = String.join("\n", errors);
                    throw new BadRequestException(errorMessage);
                }
                vs.addViaje(v);
                return ResponseEntity.status(OK).body("Viaje registrado");
            } catch (BadRequestException e) {
                return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(BAD_REQUEST).body("Hubo un error al cargar el viaje");
            } catch (Exception e) {
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
            }
        });
    }

    @GetMapping("")
    public ResponseEntity<ViajeListResponse> getAll() {
        try {
            return ResponseEntity.status(OK).body(new ViajeListResponse(vs.getAll(), "Viajes recuperados con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ViajeListResponse(null, "Hubo un error al recuperar los viajes"));
        }
    }

    @GetMapping("/total")
    public Integer getTotal() {
        return vs.getTotal();
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<Object> updateViaje(@PathVariable final @NotNull Integer id, @RequestBody final @NotNull Viaje v) {
        try {
            Viaje updatedViaje = vs.updateViaje(id, v);
            if (updatedViaje == null) {
                return ResponseEntity.status(NOT_FOUND).body("Viaje " + id + " no encontrado");
            }
            return ResponseEntity.status(OK).body(new ViajeResponse(updatedViaje, "Viaje " + id + " actualizado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<String> deleteViaje(@PathVariable final @NotNull Integer id) {
        try {
            if (vs.existsById(id)) {
                vs.deleteViaje(id);
                return ResponseEntity.status(OK).body("Viaje " + id + " eliminado con éxito");
            }
            return ResponseEntity.status(NOT_FOUND).body("Viaje " + id + " no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getViaje(@PathVariable final @NotNull Integer id) {
        try {
            Viaje viaje = vs.getViaje(id);
            if (viaje == null) {
                return ResponseEntity.status(NOT_FOUND).body("Viaje " + id + " no encontrado");
            }
            return ResponseEntity.status(OK).body(new ViajeResponse(viaje, "Viaje " + id + " recuperado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar el viaje");
        }
    }

    @GetMapping("/viaje/{inicio}")
    public ResponseEntity<Object> getViajeByInicio(@PathVariable String inicio) {
        try {
            Viaje viaje = vs.findByUbicacionInicio(inicio);
            if (viaje == null) {
                return ResponseEntity.status(NOT_FOUND).body("Viaje con inicio (" + inicio + ") no encontrado");
            }
            return ResponseEntity.status(OK).body(new ViajeResponse(viaje, "Viaje con inicio (" + inicio + ") recuperado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar el viaje");
        }
    }

    @GetMapping("/viaje/{destino}")
    public ResponseEntity<Object> getViajeByDestino(@PathVariable String destino) {
        try {
            Viaje viaje = vs.findByUbicacionDestino(destino);
            if (viaje == null) {
                return ResponseEntity.status(NOT_FOUND).body("Viaje con inicio (" + destino + ") no encontrado");
            }
            return ResponseEntity.status(OK).body(new ViajeResponse(viaje, "Viaje con inicio (" + destino + ") recuperado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar el viaje");
        }
    }
}