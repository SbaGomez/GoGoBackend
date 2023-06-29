package com.tpfinal.gogo.controller;

import com.tpfinal.gogo.exceptions.BadRequestException;
import com.tpfinal.gogo.model.Auto;
import com.tpfinal.gogo.model.AutoHistory;
import com.tpfinal.gogo.model.User;
import com.tpfinal.gogo.model.Viaje;
import com.tpfinal.gogo.service.AutoService;
import com.tpfinal.gogo.service.UserService;
import com.tpfinal.gogo.service.ViajeService;
import com.tpfinal.gogo.tools.ValidateService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("auto")
@CrossOrigin("http://localhost:19006")
public class AutoController {
    @Autowired
    private AutoService as;
    @Autowired
    private UserService us;
    @Autowired
    private ViajeService vs;

    private record AutoListResponse(List<Auto> autos, String message) {
    }

    private record AutoResponse(Auto auto, String message) {
    }

    @Async
    @PostMapping("/addAuto")
    public CompletableFuture<ResponseEntity<Object>> addAuto(@RequestBody Map<String, String> request) {
        return CompletableFuture.supplyAsync(() -> {
            Auto a = new Auto();
            a.setPatente(request.get("patente"));
            a.setColor(request.get("color"));
            a.setModelo(request.get("modelo"));
            a.setMarca(request.get("marca"));
            List<String> errors = ValidateService.validateAuto(a);
            try {
                if (!errors.isEmpty()) {
                    String errorMessage = String.join("\n", errors);
                    throw new BadRequestException(errorMessage);
                }

            Integer id = null;
            String idString = request.get("id");
            if (idString != null) {
                id = Integer.parseInt(idString);
                as.addAuto(a);
                User user = new User();
                user.setAuto(a);
                us.updateUser(id, user);

                // Create an AutoHistory object
                AutoHistory autoHistory = new AutoHistory();
                autoHistory.setPatente(a.getPatente());
                autoHistory.setColor(a.getColor());
                autoHistory.setModelo(a.getModelo());
                autoHistory.setMarca(a.getMarca());
                autoHistory.setAuto(a);
                autoHistory.setCreationDate(LocalDateTime.now());
                autoHistory.setUser(us.getUser(id));

                // Save AutoHistory to the repository
                as.addAutoHistory(autoHistory);

                return ResponseEntity.status(OK).body(a);

            } else { return ResponseEntity.status(BAD_REQUEST).body("Id de usuario no encontrado"); }

            } catch (BadRequestException e) {
                return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(BAD_REQUEST).body("Hubo un error al cargar el auto");
            } catch (Exception e) {
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
            }
        });
    }

    @GetMapping("")
    public ResponseEntity<AutoListResponse> getAll() {
        try {
            return ResponseEntity.status(OK).body(new AutoListResponse(as.getAll(), "Autos recuperados con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new AutoListResponse(null, "Hubo un error al recuperar los autos"));
        }
    }

    @GetMapping("/total")
    public Integer getTotal() {
        return as.getTotal();
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<Object> updateAuto(@PathVariable final @NotNull Integer id, @RequestBody final @NotNull Auto a) {
        try {
            Auto updatedAuto = as.updateAuto(id, a);
            if (updatedAuto == null) {
                return ResponseEntity.status(NOT_FOUND).body("Auto " + id + " no encontrado");
            }
            return ResponseEntity.status(OK).body(new AutoResponse(updatedAuto, "Auto " + id + " actualizado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<String> deleteAuto(@PathVariable final @NotNull Integer id) {
        try {
            Auto auto = as.getAuto(id);
            if (auto != null) {
                List<Viaje> viajes = vs.getViajesByAutoId(id);
                LocalDateTime now = LocalDateTime.now();
                boolean hasNonExpiredViajes = false;
                for (Viaje viaje : viajes) {
                    if (viaje.getExpirationDate().isAfter(now)) {
                        hasNonExpiredViajes = true;
                        break;
                    }
                }
                if (viajes.isEmpty() || !hasNonExpiredViajes) {
                    User user = auto.getUser();
                    user.setAuto(null);
                    us.updateUser(user.getId(), user);

                    // Create an AutoHistory object
                    AutoHistory autoHistory = new AutoHistory();
                    autoHistory.setAuto(null);
                    autoHistory.setDeletionDate(LocalDateTime.now());

                    // Save AutoHistory to the repository
                    as.updateAutoHistory(id, autoHistory);

                    as.deleteAuto(id);
                    return ResponseEntity.status(OK).body("Auto " + id + " eliminado con éxito");
                } else {
                    return ResponseEntity.status(BAD_REQUEST).body("El Auto tiene un viaje en progreso");
                }
            }
            return ResponseEntity.status(NOT_FOUND).body("Auto " + id + " no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAuto(@PathVariable final @NotNull Integer id) {
        try {
            Auto auto = as.getAuto(id);
            if (auto == null) {
                return ResponseEntity.status(NOT_FOUND).body("Auto " + id + " no encontrado");
            }
            return ResponseEntity.status(OK).body(auto);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar el auto");
        }
    }

    @GetMapping("/patente/{patente}")
    public ResponseEntity<Object> getAutoByPatente(@PathVariable final @NotNull String patente) {
        try {
            Auto auto = as.findByPatente(patente);
            if (auto == null) {
                return ResponseEntity.status(NOT_FOUND).body("Auto con patente (" + patente + ") no encontrado");
            }
            return ResponseEntity.status(OK).body(new AutoResponse(auto, "Auto con patente (" + patente + ") recuperado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Hubo un error al recuperar el auto");
        }
    }
}
