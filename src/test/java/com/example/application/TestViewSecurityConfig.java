package com.example.application;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.application.data.service.MockSampleAddressService;
import com.example.application.data.service.MockUserRepository;
import com.example.application.data.service.SampleAddressService;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.login.LoginView;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.spring.SpringViewAccessChecker;

@Configuration
public class TestViewSecurityConfig {

//    @Bean
//    ViewAccessChecker viewAccessChecker() {
//        return new SpringViewAccessChecker(new AccessAnnotationChecker());
//    }

    @Bean
    VaadinServiceInitListener setupViewSecurityScenario() {
        SpringViewAccessChecker viewAccessChecker = new SpringViewAccessChecker(new AccessAnnotationChecker());
        viewAccessChecker.setLoginView(LoginView.class);
        return event -> {
            event.getSource().addUIInitListener(uiEvent -> {
                uiEvent.getUI().addBeforeEnterListener(viewAccessChecker);
            });
        };
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
    UserDetailsService mockUserDetailsService() {

        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username)
                    throws UsernameNotFoundException {
                if ("user".equals(username)) {
                    return new User(username, UUID.randomUUID().toString(),
                            List.of(new SimpleGrantedAuthority("ROLE_DEV"),
                                    new SimpleGrantedAuthority("ROLE_USER")));
                }
                if ("admin".equals(username)) {
                    return new User(username, UUID.randomUUID().toString(),
                            List.of(new SimpleGrantedAuthority(
                                    "ROLE_SUPERUSER"),
                                    new SimpleGrantedAuthority("ROLE_ADMIN")));
                }
                throw new UsernameNotFoundException(
                        "User " + username + " not exists");
            }
        };
    }

    @Bean
    AuthenticatedUser mockAuthenticatedUser() {
        return new AuthenticatedUser(new MockUserRepository());
    }
}
