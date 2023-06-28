package com.tpfinal.gogo.controller;

import com.tpfinal.gogo.exceptions.BadRequestException;
import com.tpfinal.gogo.model.Viaje;
import com.tpfinal.gogo.model.ViajeUserAuto;
import com.tpfinal.gogo.service.UserService;
import com.tpfinal.gogo.service.ViajeService;
import com.tpfinal.gogo.tools.ValidateService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private UserService us;

    private record ViajeListResponse(List<Viaje> viajes, String message) {
    }

    private record ViajeResponse(Viaje viaje, String message) {
    }

    @Async
    @PostMapping("/addViaje")
    public CompletableFuture<ResponseEntity<Object>> addViaje(@RequestBody Map<String, String> request) {
        return CompletableFuture.supplyAsync(() -> {
            Viaje v = new Viaje();
            v.setHorarioSalida(LocalDateTime.parse(request.get("horarioSalida")));
            v.setTurno(request.get("turno"));

            String inicio = request.get("inicio");
            String destino = request.get("destino");

            int userId = Integer.parseInt(request.get("userId"));
            int autoId = Integer.parseInt(request.get("autoId"));

            if (vs.existsByNombre(inicio)) {
                v.setUbicacionInicio(inicio);
            }
            if (vs.existsByNombre(destino)) {
                v.setUbicacionDestino(destino);
            }

            List<String> errors = ValidateService.validateViaje(v);
            if (!errors.isEmpty()) {
                String errorMessage = String.join("\n", errors);
                throw new BadRequestException(errorMessage);
            }
            try {
                v.setChofer(userId);
                v.setAutoId(autoId);

                vs.addViaje(v);

                return ResponseEntity.status(OK).body(v);
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
    public ResponseEntity<Object> getViajeUserAuto(@PathVariable final @NotNull Integer id) {
        try {
            ViajeUserAuto viaje = vs.getViajeUserAuto(id);
            if (viaje == null) {
                return ResponseEntity.status(NOT_FOUND).body("Viaje " + id + " no encontrado");
            }
            return ResponseEntity.status(OK).body(viaje);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar el viaje");
        }
    }

    @Async
    @GetMapping("/buscarMisViajes/{userId}")
    public CompletableFuture<ResponseEntity<Object>> getMisViajes(@PathVariable final @NotNull Integer userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ViajeUserAuto> viajes = vs.findMisViajesById(userId);
                if (viajes.isEmpty()) {
                    return ResponseEntity.status(NOT_FOUND).body("No se encontraron viajes");
                }
                return ResponseEntity.status(OK).body(viajes);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar los viajes");
            }
        });
    }

    @Async
    @GetMapping("/buscarUbicacion/{ubicacionInicioBuscarViaje}/{ubicacionDestinoBuscarViaje}")
    public CompletableFuture<ResponseEntity<Object>> getViajeByUbicacion(@PathVariable final @NotNull String ubicacionInicioBuscarViaje,
                                                                         @PathVariable final @NotNull String ubicacionDestinoBuscarViaje) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ViajeUserAuto> viajes = vs.findByUbicacion(ubicacionInicioBuscarViaje, ubicacionDestinoBuscarViaje);
                if (viajes.isEmpty()) {
                    return ResponseEntity.status(NOT_FOUND).body("No se encontraron viajes");
                }
                return ResponseEntity.status(OK).body(viajes);
            } catch (Exception e) {
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar los viajes");
            }
        });
    }

    @Async
    @PostMapping("/{userId}/{viajeId}/joinViaje")
    public CompletableFuture<ResponseEntity<Object>> joinViaje(@PathVariable final @NotNull Integer userId, @PathVariable final @NotNull Integer viajeId ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Viaje viaje = vs.getViaje(viajeId);
                List<Integer> newUsers = new ArrayList<Integer>();
                List<Integer> users = viaje.getUsers();
                if (userId != viaje.getChofer()) {
                    if (users != null) {
                        if (users.contains(userId)) {
                            return ResponseEntity.status(CONFLICT).body("El usuario ya está unido al viaje");
                        }
                        users.add(userId);
                        viaje.setUsers(users);
                    }
                    if (users == null) {
                        newUsers.add(userId);
                        viaje.setUsers(newUsers);
                    }
                    vs.joinLeaveViaje(viajeId, viaje);
                    return ResponseEntity.status(OK).body(new ViajeResponse(viaje, "Viaje " + viajeId + " actualizado con éxito"));
                }
                return ResponseEntity.status(CONFLICT).body("El usuario no se puede unir a su propio viaje");
            } catch (Exception e) {
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
            }
        });
    }

    @Async
    @PostMapping("/{userId}/{viajeId}/leaveViaje")
    public CompletableFuture<ResponseEntity<Object>> leaveViaje(@PathVariable final @NotNull Integer userId, @PathVariable final @NotNull Integer viajeId ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Viaje viaje = vs.getViaje(viajeId);
                List<Integer> users = viaje.getUsers();
                if (users != null) {
                    users.remove(userId);
                    viaje.setUsers(users);
                    vs.joinLeaveViaje(viajeId, viaje);
                    return ResponseEntity.status(OK).body("Viaje cancelado con éxito");
                }
                return ResponseEntity.status(NOT_FOUND).body("Usuario no encontrado");
            } catch (Exception e) {
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
            }
        });
    }

}
