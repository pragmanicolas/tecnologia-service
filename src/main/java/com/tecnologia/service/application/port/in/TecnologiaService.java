package com.tecnologia.service.application.port.in;

import com.tecnologia.service.domain.Tecnologia;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TecnologiaService {

    Mono<Tecnologia> registrarTecnologia(Tecnologia tecnologia);

    Flux<Tecnologia> listarTecnologias(boolean ascending, int page, int size);

    Mono<Tecnologia> obtenerTecnologiaPorId(Long id);

    Mono<Void> eliminarTecnologia(Long id);
}
