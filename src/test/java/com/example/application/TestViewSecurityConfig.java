package com.example.application;

import java.io.Serializable;
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
public class TestViewSecurityConfig implements Serializable {

    private transient NavigationAccessControl navigationAccessControl = new SpringNavigationAccessControl();
    private transient AnnotatedViewAccessChecker annotatedViewAccessChecker = new AnnotatedViewAccessChecker(
            new AccessAnnotationChecker());
    private transient AccessAnnotationChecker accessAnnotationChecker = new AccessAnnotationChecker();
    private transient SampleAddressService sampleAddressService = new MockSampleAddressService();

    @Bean
    NavigationAccessControl navigationAccessControl() {
        return navigationAccessControl;
    }

    @Bean
    AnnotatedViewAccessChecker viewAccessChecker() {
        return annotatedViewAccessChecker;
    }

    @Bean
    AccessAnnotationChecker mockAccessAnnotationChecker() {
        return accessAnnotationChecker;
    }

    @Bean
    SampleAddressService myService() {
        return sampleAddressService;
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
    public static class MockAuthenticatedUser implements AuthenticatedUser {

        @Override
        public Optional<com.example.application.data.entity.User> get() {
            return Optional.empty();
        }

        @Override
        public void logout() {
        }

    }
}
