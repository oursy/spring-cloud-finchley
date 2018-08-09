package com.example.order.web.rest;


import com.example.order.service.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
@Slf4j
public class OrderResource {

    private final ProductClient productClient;


    @GetMapping(value = "/orders")
    public ResponseEntity<?> orders() {
        ResponseEntity<?> products = productClient.products();
        log.debug("products :{}", products.getBody());
        return products;
    }
}


