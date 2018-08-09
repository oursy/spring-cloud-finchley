package com.example.order.service;


import com.example.order.client.UserFeignClientInterceptor;
import feign.FeignException;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "product-service", configuration = UserFeignClientInterceptor.class, fallbackFactory = ProductClient.ProductClientHystrix.class)
public interface ProductClient {


    @GetMapping(value = "/api/products")
    ResponseEntity<?> products();


    @Component
    @Slf4j
    public class ProductClientHystrix implements FallbackFactory<ProductClient> {

        @Override
        public ProductClient create(Throwable throwable) {
            return new ProductClient() {
                @Override
                public ResponseEntity<?> products() {
                    log.error("call /api/products ,errors :{}", throwable);
                    return cause(throwable);
                }
            };
        }

        ResponseEntity<?> cause(Throwable cause) {
            if (cause instanceof FeignException
                    && ((FeignException) cause).status() == 403) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }
        }

    }


}

