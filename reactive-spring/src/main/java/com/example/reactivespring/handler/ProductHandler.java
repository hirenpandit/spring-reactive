package com.example.reactivespring.handler;

import com.example.reactivespring.entity.Product;
import com.example.reactivespring.entity.ProductEvent;
import com.example.reactivespring.repository.ProductRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ProductHandler {

    private final ProductRepository productRepository;

    public ProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<ServerResponse> getAllProducts(ServerRequest serverRequest) {
        Flux<Product> products = productRepository.findAll();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(products, Product.class);
    }

    public Mono<ServerResponse> getProduct(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Product> productMono = productRepository.findById(id);
        return productMono.flatMap(product -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productMono, Product.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);
        return productMono.flatMap(product ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(productRepository.save(product), Product.class));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Product> existingProductMono = productRepository.findById(id);
        Mono<Product> productMono = request.bodyToMono(Product.class);
        Mono<ServerResponse> notFoundMono = ServerResponse.notFound().build();
        return productMono
                .zipWith(existingProductMono,
                        (product, existing) -> new Product(existing.getId(), product.getName(), product.getPrice()))
                .flatMap(p -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p, Product.class))
                .switchIfEmpty(notFoundMono);

    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<ServerResponse> notFoundMono = ServerResponse.notFound().build();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build(productRepository.deleteById(id))
                .switchIfEmpty(notFoundMono);
    }

    public Mono<ServerResponse> deleteAll(ServerRequest request) {
        return ServerResponse.ok()
                .build(productRepository.deleteAll());
    }

    public Mono<ServerResponse> getProductEvents(ServerRequest reqeust) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(Flux.interval(Duration.ofSeconds(1))
                        .map(val -> new ProductEvent(val, "Product Event")), ProductEvent.class);
    }
}
