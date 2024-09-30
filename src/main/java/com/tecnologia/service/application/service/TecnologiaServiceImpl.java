package com.tecnologia.service.application.service;

import com.tecnologia.service.adapters.out.persistence.TecnologiaEntity;
import com.tecnologia.service.application.exception.InvalidTecnologiaDataException;
import com.tecnologia.service.application.exception.TecnologiaAlreadyExistsException;
import com.tecnologia.service.application.port.in.TecnologiaService;
import com.tecnologia.service.application.port.out.TecnologiaRepository;
import com.tecnologia.service.domain.Tecnologia;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TecnologiaServiceImpl implements TecnologiaService {



    private final TecnologiaRepository tecnologiaRepository;

    public TecnologiaServiceImpl(TecnologiaRepository tecnologiaRepository) {
        this.tecnologiaRepository = tecnologiaRepository;
    }

    private Mono<Void> validateTecnologia(Tecnologia tecnologia) {
        if (tecnologia.getNombre() == null || tecnologia.getNombre().isEmpty()) {
            return Mono.error(new InvalidTecnologiaDataException("El nombre de la tecnología es obligatorio"));
        }
        if (tecnologia.getNombre().length() > 50) {
            return Mono.error(new InvalidTecnologiaDataException("El nombre de la tecnología no puede tener más de 50 caracteres"));
        }
        if (tecnologia.getDescripcion() == null || tecnologia.getDescripcion().isEmpty()) {
            return Mono.error(new InvalidTecnologiaDataException("La descripción de la tecnología es obligatoria"));
        }
        if (tecnologia.getDescripcion().length() > 90) {
            return Mono.error(new InvalidTecnologiaDataException("La descripción de la tecnología no puede tener más de 90 caracteres"));
        }
        return Mono.empty();
    }

    @Override
    public Mono<Tecnologia> registrarTecnologia(Tecnologia tecnologia) {
        return validateTecnologia(tecnologia)
                .then(tecnologiaRepository.existsByName(tecnologia.getNombre()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new TecnologiaAlreadyExistsException("El nombre de la tecnología ya existe"));
                    }
                    return tecnologiaRepository.save(tecnologia);
                });
    }

    @Override
    public Flux<Tecnologia> listarTecnologias(boolean ascending, int page, int size) {
        // Listar todas las tecnologías con ordenación y paginación
        return tecnologiaRepository.findAllSortedByName(ascending, page, size);
    }

    @Override
    public Mono<Tecnologia> obtenerTecnologiaPorId(Long id) {
        // Obtener tecnología por ID
        return tecnologiaRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Tecnología no encontrada")));
    }

    @Override
    public Mono<Void> eliminarTecnologia(Long id) {
        // Eliminar tecnología por ID
        return tecnologiaRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Tecnología no encontrada")))
                .flatMap(tecnologia -> tecnologiaRepository.deleteById(id));
    }
}

