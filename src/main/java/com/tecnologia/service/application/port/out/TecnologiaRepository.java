package com.tecnologia.service.application.port.out;

import com.tecnologia.service.adapters.out.persistence.TecnologiaEntity;
import com.tecnologia.service.domain.Tecnologia;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TecnologiaRepository{


    Mono<Tecnologia> save(Tecnologia tecnologia);

    Mono<Tecnologia> findById(Long id);

    Flux<Tecnologia> findAll();

    Mono<Void> deleteById(Long id);

    Flux<Tecnologia> findAllSortedByName(boolean ascending, int page, int size);

    Mono<Boolean> existsByName(String name);
}
