package com.tecnologia.service.adapters.out.persistence;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<UserEntity, String> {
    Mono<UserEntity> findByUsername(String username);
}
