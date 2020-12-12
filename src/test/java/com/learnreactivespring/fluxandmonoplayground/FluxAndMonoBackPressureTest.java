package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void backPressureTest() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();

    }

    @Test
    public void backPressure() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe(integer -> System.out.println("Element is: " + integer),
                throwable -> System.out.println("Exception is ; " + throwable),
                () -> System.out.println("Done"),
                subscription -> subscription.request(2));
    }

    @Test
    public void backPressure_Cancel() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe(integer -> System.out.println("Element is: " + integer),
                throwable -> System.out.println("Exception is ; " + throwable),
                () -> System.out.println("Done"),
                Subscription::cancel);
    }

    @Test
    public void customized_backPressure() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe(new BaseSubscriber<Integer>() {

            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Vale received is : " + value);
                if (value == 4) {
                    cancel();
                }
            }

        });

    }

}
