package com.tecnologia.service.adapters.out.persistence;

import com.tecnologia.service.application.port.out.TecnologiaRepository;
import com.tecnologia.service.domain.Tecnologia;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TecnologiaRepositoryImpl implements TecnologiaRepository {

    private final SpringDataTecnologiaRepository springDataTecnologiaRepository;

    public TecnologiaRepositoryImpl(SpringDataTecnologiaRepository springDataTecnologiaRepository){
        this.springDataTecnologiaRepository = springDataTecnologiaRepository;
    }

    @Override
    public Mono<Tecnologia> save(Tecnologia tecnologia) {
        // Convertir de Tecnologia a TecnologiaEntity antes de guardar
        TecnologiaEntity entity = new TecnologiaEntity(tecnologia.getId(), tecnologia.getNombre(), tecnologia.getDescripcion());
        return springDataTecnologiaRepository.save(entity)
                // Convertir de TecnologiaEntity a Tecnología después de guardar
                .map(savedEntity -> new Tecnologia(savedEntity.getId(), savedEntity.getNombre(), savedEntity.getDescripcion()));
    }

    @Override
    public Mono<Tecnologia> findById(Long id) {
        // Convertir de TecnologiaEntity a Tecnologia al buscar por ID
        return springDataTecnologiaRepository.findById(id)
                .map(entity -> new Tecnologia(entity.getId(), entity.getNombre(), entity.getDescripcion()));
    }

    @Override
    public Flux<Tecnologia> findAll() {
        // Convertir de TecnologiaEntity a Tecnologia al recuperar los datos
        return springDataTecnologiaRepository.findAll()
                .map(entity -> new Tecnologia(entity.getId(), entity.getNombre(), entity.getDescripcion()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return springDataTecnologiaRepository.deleteById(id);
    }

    @Override
    public Flux<Tecnologia> findAllSortedByName(boolean ascending, int page, int size) {
        long offset = (long) page * size;
        Flux<TecnologiaEntity> entityFlux = ascending
                ? springDataTecnologiaRepository.findAllByOrderByNombreAsc(size, offset)
                : springDataTecnologiaRepository.findAllByOrderByNombreDesc(size, offset);

        return entityFlux.map(entity -> new Tecnologia(entity.getId(), entity.getNombre(), entity.getDescripcion()));
    }



    @Override
    public Mono<Boolean> existsByName(String name) {
        return springDataTecnologiaRepository.existsByNombre(name);
    }
}
