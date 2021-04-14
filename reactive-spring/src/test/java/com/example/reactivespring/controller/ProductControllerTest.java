package com.example.reactivespring.controller;

import com.example.reactivespring.entity.Product;
import com.example.reactivespring.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest
public class ProductControllerTest {

    private WebTestClient client;

    private List<Product> expectedList;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setup() {
        this.client = WebTestClient
                .bindToController(new ProductController(repository))
                .configureClient()
                .baseUrl("/products")
                .build();

        this.expectedList = repository.findAll().collectList().block();
    }

    @Test
    void testGetAllProducts () {
        client
                .get()
                .uri("/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Product.class)
                .isEqualTo(expectedList);
    }

    @Test
    void testProductIdNotFound() {
        client
                .get()
                .uri("/aaa")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testProductIdFound() {
        Product expecteProduct = this.expectedList.get(0);
        client
                .get()
                .uri("/{id}", expecteProduct.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Product.class)
                .isEqualTo(expecteProduct);
    }

}
