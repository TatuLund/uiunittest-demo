package com.example.application.views.addresses;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.testbench.unit.SpringUIUnit4Test;

import com.example.application.TestViewSecurityConfig;
import com.example.application.data.entity.SampleAddress;

@ContextConfiguration(classes = TestViewSecurityConfig.class)
public class AddressesViewTest extends SpringUIUnit4Test {

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void addItemRemoveItem() {

        // Navigate to AddressesView
        navigate(AddressesView.class);

        // Populate form
        $(TextField.class).withCaption("Street").first().setValue("Ruukkikatu 2-4");
        $(TextField.class).withCaption("Postal Code").first().setValue("20540");
        $(TextField.class).withCaption("City").first().setValue("Turku");
        $(TextField.class).withCaption("State").first().setValue("-");
        $(TextField.class).withCaption("Country").first().setValue("Finland");
        
        // Click save button
        $(Button.class).withCaption("Save").first().click();

        // Notification will appear
        $(Notification.class).first();

        // Assert that form is empty
        assertFormIsEmpty();
        
        // Pick last item from the Grid data provider
        Grid<SampleAddress> grid = $(Grid.class).first();
        GridLazyDataView<SampleAddress> dataView = grid.getLazyDataView();
        SampleAddress newItem = dataView.getItem(500);

        // Assert that item is the same as we entered
        assertEquals("Ruukkikatu 2-4", newItem.getStreet());
        assertEquals("20540", newItem.getPostalCode());
        assertEquals("Turku", newItem.getCity());
        assertEquals("-", newItem.getState());
        assertEquals("Finland", newItem.getCountry());

        // Select the item
        grid.select(newItem);

        // Assert that form is correctly populated
        assertEquals("Ruukkikatu 2-4", $(TextField.class).withCaption("Street").first().getValue());
        assertEquals("20540", $(TextField.class).withCaption("Postal Code").first().getValue());
        assertEquals("Turku", $(TextField.class).withCaption("City").first().getValue());
        assertEquals("-", $(TextField.class).withCaption("State").first().getValue());
        assertEquals("Finland", $(TextField.class).withCaption("Country").first().getValue());

        // Click to delete
        $(Button.class).withCaption("Delete").first().click();
        $(Notification.class).first();

        // Assert that form is empty
        assertFormIsEmpty();

        // Assert that data provider does not have the item
        assertFalse(dataView.getItems().anyMatch(address -> address.equals(newItem)));
    }

    private void assertFormIsEmpty() {
        assertEquals("", $(TextField.class).withCaption("Street").first().getValue());
        assertEquals("", $(TextField.class).withCaption("Postal Code").first().getValue());
        assertEquals("", $(TextField.class).withCaption("City").first().getValue());
        assertEquals("", $(TextField.class).withCaption("State").first().getValue());
        assertEquals("", $(TextField.class).withCaption("Country").first().getValue());

    }

}
