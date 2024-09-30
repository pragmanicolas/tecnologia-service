package com.tecnologia.service.application.service;

import com.tecnologia.service.application.port.out.TecnologiaRepository;
import com.tecnologia.service.domain.Tecnologia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TecnologiaServiceImplTest {
    @Mock
    private TecnologiaRepository tecnologiaRepository;

    @InjectMocks
    private TecnologiaServiceImpl tecnologiaService;

    @Test
    void testCreateBootcamp() {
        Tecnologia tecnologia = new Tecnologia(null, "Java Technology", "Learn Java");
        when(tecnologiaRepository.save(any())).thenReturn(Mono.just(tecnologia));

        Mono<Tecnologia> result = tecnologiaRepository.save(tecnologia);

        StepVerifier.create(result)
                .expectNext(tecnologia)
                .verifyComplete();

        verify(tecnologiaRepository, times(1)).save(tecnologia);
    }
}
