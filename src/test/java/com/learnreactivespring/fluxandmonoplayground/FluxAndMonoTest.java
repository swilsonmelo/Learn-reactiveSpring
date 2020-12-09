package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {

        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Spring")
                //.concatWith(Flux.error(new RuntimeException("Exception Ocurred")))
                .concatWith(Flux.just("After Error"))
                .log();


        stringFlux
                .subscribe(System.out::println,
                        (e) -> System.err.println("Exceptions is " + e),
                        () -> System.out.println("Completed"));


    }

}
