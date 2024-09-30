package com.tecnologia.service.adapters.out.persistence;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataTecnologiaRepository extends ReactiveCrudRepository<TecnologiaEntity, Long> {

    Mono<Boolean> existsByNombre(String nombre);

    @Query("SELECT * FROM tecnologias ORDER BY nombre ASC LIMIT :size OFFSET :offset")
    Flux<TecnologiaEntity> findAllByOrderByNombreAsc(@Param("size") int size, @Param("offset") long offset);

    @Query("SELECT * FROM tecnologias ORDER BY nombre DESC LIMIT :size OFFSET :offset")
    Flux<TecnologiaEntity> findAllByOrderByNombreDesc(@Param("size") int size, @Param("offset") long offset);
}

