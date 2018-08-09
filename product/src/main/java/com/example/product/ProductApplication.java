package com.example.product;

import com.example.product.security.jwt.JWTConfigurer;
import com.example.product.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


@EnableDiscoveryClient
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }


    @EnableWebSecurity
    @Order(1)
    public class SecurityActuatorConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf()
                    .disable()
                    .antMatcher("/actuator/**")
                    .httpBasic()
                    .and()
                    .authorizeRequests()
                    .requestMatchers(EndpointRequest.toAnyEndpoint()).authenticated();


        }
    }

    @EnableWebSecurity
    @RequiredArgsConstructor
    public class SecuritySecureApiConfig extends WebSecurityConfigurerAdapter {
        private final TokenProvider tokenProvider;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf()
                    .disable()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/**").authenticated()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .apply(securityConfigurerAdapter());
        }

        private JWTConfigurer securityConfigurerAdapter() {
            return new JWTConfigurer(tokenProvider);
        }
    }

}
