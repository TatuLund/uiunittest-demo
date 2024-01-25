package com.example.application.views;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;

// Session scoped bean for just demonstrating
@VaadinSessionScope
public class SessionStore {

    private String attribute;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
