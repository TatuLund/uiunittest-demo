package com.example.application.views;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;

// Session scoped bean for just demonstrating
@VaadinSessionScope
@Component
public class SessionStore implements Serializable {

    private String attribute;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
