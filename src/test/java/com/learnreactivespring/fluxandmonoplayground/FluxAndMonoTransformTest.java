package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(String::toUpperCase) // ADAM, ANNA, JACK, JENNY
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();

    }

    @Test
    public void transformUsingMap_Length() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(String::length) // 4, 4, 4, 5
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();

    }

    @Test
    public void transformUsingMap_Length_Repeat() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(String::length)
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();

    }

    @Test
    public void transformUsingMap_Filter() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .map(String::toUpperCase) //JENNY
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();

    }

    @Test
    public void transformUsingFlatMap(){

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .flatMap(s -> {

                    return Flux.fromIterable(convertToList(s));// A -> List[A, newValue]
                })
                .log(); //Db or external calls for each element that returns a Flux: s -> Flux<String>

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();

    }

    private List<String> convertToList(String s) {

        try {
            Thread.sleep(1000); // we are simulating a db call
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "new value");

    }

    @Test
    public void transformUsingParallel(){

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F")) // Flux<String>
                .window(2) // Flux<Flux<String>> (A,B), (C,D) it are going to wait for two items
                .flatMap(s ->
                    s.map(this::convertToList).subscribeOn(parallel()) // Flux<List<String>>
                            .flatMap(Flux::fromIterable) // Flux<String>
                )
                .log(); //Db or external calls for each element that returns a Flux: s -> Flux<String>

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();

    }

    // Holy ****, mind blow...
    @Test
    public void transformUsing_Parallel_Maintain_Order(){

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F")) // Flux<String>
                .window(2) // Flux<Flux<String>> (A,B), (C,D) it are going to wait for two items
                /*.concatMap(s -> // Slower
                        s.map(this::convertToList).subscribeOn(parallel()) // Flux<List<String>>
                                .flatMap(Flux::fromIterable) // Flux<String>
                )*/
                .flatMapSequential(s -> // Faster
                        s.map(this::convertToList).subscribeOn(parallel()) // Flux<List<String>>
                                .flatMap(Flux::fromIterable) // Flux<String>
                )
                .log(); //Db or external calls for each element that returns a Flux: s -> Flux<String>

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();

    }

}
