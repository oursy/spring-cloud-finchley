package com.example.gateway.config;

import com.example.gateway.security.jwt.JWTConfigurer;
import com.example.gateway.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Configuration
    @Order(1)
    public class SecurityActuatorConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf()
                    .disable()
                    .antMatcher("/actuator/**")
                    .authorizeRequests()
                    .requestMatchers(EndpointRequest.toAnyEndpoint()).authenticated()
                    .and()
                    .httpBasic();

        }

        @Bean
        public UserDetailsService actuatorDetailsService() {
            // ensure the passwords are encoded properly
            User.UserBuilder users = User.withDefaultPasswordEncoder();
            InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
            manager.createUser(users.username("admin").password("password").roles("USER", "ADMIN").build());
            return manager;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
            daoAuthenticationProvider.setUserDetailsService(actuatorDetailsService());
            auth.authenticationProvider(daoAuthenticationProvider);
        }
    }


    @Configuration
    @RequiredArgsConstructor
    public class SecuritySecureApiConfig extends WebSecurityConfigurerAdapter {

        private final TokenProvider tokenProvider;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .exceptionHandling()
                    .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                    .and()
                    .csrf()
                    .disable()
                    .headers()
                    .frameOptions()
                    .disable()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/authenticate").permitAll()
                    .antMatchers("/api/**").authenticated()
                    .and()
                    .apply(securityConfigurerAdapter());

        }


        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Bean
        public UserDetailsService userDetailsService() {
            // ensure the passwords are encoded properly
            User.UserBuilder users = User.withDefaultPasswordEncoder();
            InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
            manager.createUser(users.username("user").password("user").roles("USER").build());
            manager.createUser(users.username("admin").password("admin").roles("USER", "ADMIN").build());
            return manager;
        }


        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
            daoAuthenticationProvider.setUserDetailsService(userDetailsService());
            auth.authenticationProvider(daoAuthenticationProvider);
        }

        private JWTConfigurer securityConfigurerAdapter() {
            return new JWTConfigurer(tokenProvider);
        }
    }

}
