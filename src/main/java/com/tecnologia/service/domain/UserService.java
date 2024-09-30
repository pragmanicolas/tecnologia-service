package com.tecnologia.service.domain;

import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> findByUsername(String username);  // Aquí usas Mono ya que la aplicación es reactiva
}
