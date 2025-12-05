package com.example.application.security;

import com.example.application.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfiguration {

    public static final String LOGOUT_URL = "/";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /**
         * Delegating the responsibility of general configuration
         * of HTTP security to the VaadinSecurityConfigurer.
         *
         * It's configuring the following:
         * - Vaadin's CSRF protection by ignoring internal framework requests,
         * - default request cache,
         * - ignoring public views annotated with @AnonymousAllowed,
         * - restricting access to other views/endpoints, and
         * - enabling ViewAccessChecker authorization.
         */

        // You can add any possible extra configurations of your own
        // here - the following is just an example:
        http.rememberMe(customizer -> customizer.alwaysRemember(false));

        // Configure your static resources with public access before calling
        // VaadinSecurityConfigurer.vaadin() as it adds final anyRequest matcher
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/admin-only/**").hasAnyRole("admin")
                    .requestMatchers("/line-awesome/svg/address-card.svg").permitAll()
                    .requestMatchers("/views/addresses-view.css").permitAll()
                    .requestMatchers("/main-layout.css").permitAll()
                    .requestMatchers("/line-awesome/dist/line-awesome/css/line-awesome.min.css").permitAll()
                    .requestMatchers("/public/**").permitAll();
        });

        http.with(VaadinSecurityConfigurer.vaadin(), configurer -> {
            // This is important to register your login view to the
            // view access checker mechanism:
            configurer.loginView(LoginView.class, LOGOUT_URL);
        });

        return http.build();
    }

}
