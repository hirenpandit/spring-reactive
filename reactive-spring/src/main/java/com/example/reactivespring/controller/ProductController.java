package com.example.reactivespring.controller;

import com.example.reactivespring.entity.Product;
import com.example.reactivespring.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/products")
public class ProductController {

    private final ProductRepository repository;

    @GetMapping("/")
    public Flux<Product> getAll(){
        log.info("Getting all products");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getById(@PathVariable String id) {
        log.info("Getting product by id: {}", id);
        return repository.findById(id)
                .map(product -> ResponseEntity.ok().body(product))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    @DeleteMapping("/")
    public Mono<Void> deleteAll(){
        log.info("Deleting all products");
        return repository.deleteAll();
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable String id){
        log.info("Deleting product id: {}",id);
        return repository.deleteById(id);
    }

    @PostMapping
    public Mono<Product> save(@RequestBody Product product) {
        log.info("Saving product: {}", product);
        return repository.save(product);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Product> update(@RequestBody Product product, @PathVariable String id) {
        log.info("Saving product: {}", product);
        Mono<Product> existing = repository.findById(id);
        Mono<Product> productMono = Mono.just(product);

        return productMono
                .zipWith(existing, (a, b) -> new Product(b.getId(), a.getName(), a.getPrice()))
                .flatMap(repository::save);
    }

}
