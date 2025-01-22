package com.example.application;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.application.data.service.MockSampleAddressService;
import com.example.application.data.service.SampleAddressService;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.SessionStore;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnnotatedViewAccessChecker;
import com.vaadin.flow.server.auth.NavigationAccessControl;
import com.vaadin.flow.spring.security.SpringNavigationAccessControl;

@Configuration
public class TestViewSecurityConfig {

    @Bean
    NavigationAccessControl navigationAccessControl() {
        return new SpringNavigationAccessControl();
    }

    @Bean
    AnnotatedViewAccessChecker viewAccessChecker() {
        return new AnnotatedViewAccessChecker(new AccessAnnotationChecker());
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
