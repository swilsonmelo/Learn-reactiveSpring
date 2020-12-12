package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class HotAndColdPublisherTest {

    @Test
    public void coldPublisherTest() throws InterruptedException {

        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1))
                .log();

        stringFlux.subscribe(s -> System.out.println("suscirber 1 : " + s)); //emits value from the beginning

        Thread.sleep(2000);

        stringFlux.subscribe(s -> System.out.println("suscirber 2 : " + s)); //emits value from the beginning

        Thread.sleep(4000);

    }

    @Test
    public void hotPublisherTest() throws InterruptedException {

        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1))
                .log();

        ConnectableFlux<String> stringConnectableFlux = stringFlux.publish();
        stringConnectableFlux.connect();
        stringConnectableFlux.subscribe(s -> System.out.println("suscirber 1 : " + s));

        Thread.sleep(3000);

        stringConnectableFlux.subscribe(s -> System.out.println("suscirber 2 : " + s));
        // does not emit the values from the beginning

        Thread.sleep(4000);

    }

}
