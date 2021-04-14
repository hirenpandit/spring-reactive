package com.example.reactivespring;

import com.example.reactivespring.entity.Product;
import com.example.reactivespring.entity.Reservation;
import com.example.reactivespring.handler.ProductHandler;
import com.example.reactivespring.repository.ProductRepository;
import com.example.reactivespring.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class ReactiveSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveSpringApplication.class, args);
    }

}

@Component
@RequiredArgsConstructor
class DataInitializer {
    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        var saved = Flux.just("Glenn", "Simon", "Josh", "Hiren", "Madhura", "Bob", "Violetta")
                .map(name -> new Reservation(null, name))
                .flatMap(r -> this.reservationRepository.save(r));

        saved.subscribe();

        this.reservationRepository
                .deleteAll()
                .thenMany(saved)
                .thenMany(this.reservationRepository.findAll())
                .subscribe(System.out::println);

        Flux<Product> productFlux = Flux.just(
                new Product(null, "Big Latte", 2.99),
                new Product(null, "Big Decaf", 2.49),
                new Product(null, "Green Tea", 1.99))
                .flatMap(productRepository::save);
        productFlux.thenMany(productRepository.findAll())
                .subscribe(System.out::println);
    }

    @Bean
    RouterFunction<ServerResponse> routes(ProductHandler handler) {

        //two ways of defining routers for the end points
//        return RouterFunctions.route(GET("/products")
//                        .and(accept(APPLICATION_JSON)), handler::getAllProducts)
//                .andRoute(GET("/products/{id}").
//                        and(accept(APPLICATION_JSON)), handler::getProduct)
//                .andRoute(POST("/products")
//                        .and(accept(APPLICATION_JSON)), handler::save)
//                .andRoute(PUT("/products/{id}")
//                        .and(accept(APPLICATION_JSON)), handler::update)
//                .andRoute(DELETE("/products")
//                        .and(accept(APPLICATION_JSON)), handler::deleteAll)
//                .andRoute(DELETE("/products/{id}")
//                        .and(accept(APPLICATION_JSON)), handler::delete)
//                .andRoute(GET("/products/events")
//                        .and(accept(TEXT_EVENT_STREAM)), handler::getProductEvents);

        return RouterFunctions.nest(path("/products"),
                nest(accept(APPLICATION_JSON).or(accept(TEXT_EVENT_STREAM)),
                        route(GET("/"), handler::getAllProducts)
                        .andRoute(POST("/"), handler::save)
                        .andRoute(DELETE("/"), handler::deleteAll)
                        .andRoute(GET("/events"), handler::getProductEvents)
                )
        );
    }
}
