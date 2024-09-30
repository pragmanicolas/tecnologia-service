package com.tecnologia.service.adapters.in.web;

import com.tecnologia.service.application.port.in.TecnologiaService;
import com.tecnologia.service.domain.Tecnologia;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tecnologias")
public class TecnologiaController {

    private final TecnologiaService tecnologiaService;

    public TecnologiaController(TecnologiaService tecnologiaService) {
        this.tecnologiaService = tecnologiaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Tecnologia> registrarTecnologia(@Valid @RequestBody Tecnologia tecnologia) {
        return tecnologiaService.registrarTecnologia(tecnologia);
    }

    @GetMapping
    public Flux<Tecnologia> listarTecnologias(
            @RequestParam(value = "ascending", defaultValue = "true") boolean ascending,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return tecnologiaService.listarTecnologias(ascending, page, size);
    }

    @GetMapping("/{id}")
    public Mono<Tecnologia> obtenerTecnologiaPorId(@PathVariable Long id) {
        return tecnologiaService.obtenerTecnologiaPorId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminarTecnologia(@PathVariable Long id) {
        return tecnologiaService.eliminarTecnologia(id);
    }
}
