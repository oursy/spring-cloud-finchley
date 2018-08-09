package com.example.product.web.rest;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping(value = "/api")
public class ProductResource {


    private static final Map<String, Product> PRODUCTS = new ConcurrentHashMap<>();


    static {
        PRODUCTS.put("1", new Product("123", "测试产品1", "product EN1", "1"));
        PRODUCTS.put("2", new Product("123", "测试产品2", "product EN2", "2"));
        PRODUCTS.put("3", new Product("123", "测试产品3", "product EN3", "3"));
        PRODUCTS.put("4", new Product("123", "测试产品4", "product EN4", "5"));
    }


    @GetMapping(value = "/products", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> products() {
        return ResponseEntity.ok(PRODUCTS.values());
    }


    @Data
    @AllArgsConstructor
    public static class Product {
        private String productCode;
        private String productCN;
        private String productEN;
        private String productId;
    }
}

