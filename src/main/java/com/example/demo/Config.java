package com.example.demo;

import org.springframework.cloud.stream.binder.PartitionKeyExtractorStrategy;
import org.springframework.cloud.stream.binder.PartitionSelectorStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class Config {
    private static final Random RANDOM = new Random();

    @Bean
    public Supplier<Flux<Integer>> myProducer() {
        return () -> Flux.range(0, 100)
                .map(__ -> RANDOM.nextInt());
    }

    @Bean
    public Function<Flux<Integer>, Tuple2<Flux<MyEvent>, Flux<MyEvent>>> myProcessor() {
        return v -> {
            Flux<Integer> connectedFlux = v.publish().autoConnect(2);
            Sinks.Many<MyEvent> even = Sinks.many().unicast().onBackpressureBuffer();
            Sinks.Many<MyEvent> odd = Sinks.many().unicast().onBackpressureBuffer();

            Flux<Integer> evenFlux = connectedFlux
                    .filter(x -> x % 2 == 0)
                    .doOnNext(number -> {
                        System.out.printf("Sending even number [%d]...%n", number);
                        final Sinks.EmitResult e = even.tryEmitNext(new MyEvent(number, "EVEN"));

                        if (e != Sinks.EmitResult.OK) {
                            System.out.printf("Failed to emit EVEN with %s%n", e);
                        }
                    });

            Flux<Integer> oddFlux = connectedFlux
                    .filter(x -> x % 2 != 0)
                    .doOnNext(number -> {
                        System.out.printf("Sending odd number [%d]...%n", number);
                        final Sinks.EmitResult e = odd.tryEmitNext(new MyEvent(number, "ODD"));

                        if (e != Sinks.EmitResult.OK) {
                            System.out.printf("Failed to emit for ODD with %s%n", e);
                        }
                    });

            return Tuples.of(
                    even.asFlux().doOnSubscribe(x -> evenFlux.subscribe()),
                    odd.asFlux().doOnSubscribe(x -> oddFlux.subscribe())
            );
        };
    }

    @Bean
    public PartitionSelectorStrategy mySelectorStrategy() {
        return new MyPartitionSelector();
    }

    @Bean
    public PartitionKeyExtractorStrategy myKeyExtractorStrategy() {
        return new MyKeyExtractor();
    }

}
