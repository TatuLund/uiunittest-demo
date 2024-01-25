package com.example.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.application.data.service.MockSampleAddressService;
import com.example.application.data.service.SampleAddressService;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.SessionStore;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.ViewAccessChecker;
import com.vaadin.flow.spring.SpringViewAccessChecker;

@Configuration
public class TestViewSecurityConfig {

    @Bean
    ViewAccessChecker viewAccessChecker() {
        return new SpringViewAccessChecker(new AccessAnnotationChecker());
    }

    @Bean
    AccessAnnotationChecker mockAccessAnnotationChecker() {
        return new AccessAnnotationChecker();
    }

    @Bean
    SampleAddressService myService() {
        return new MockSampleAddressService();
    }

    @Bean
    AuthenticatedUser mockAuthenticatedUser() {
        return new MockAuthenticatedUser();
    }

    @Bean
    SessionStore mySessionStore() {
        return new SessionStore();
    }

    // Dummy authenticated user is needed to satifisfy injection, user is
    // actually faked by @WithMockUser
    public class MockAuthenticatedUser implements AuthenticatedUser {

        @Override
        public Optional<com.example.application.data.entity.User> get() {
            return Optional.empty();
        }

        @Override
        public void logout() {
        }

    }
}
