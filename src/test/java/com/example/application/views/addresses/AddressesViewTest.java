package com.example.application.views.addresses;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridTester;
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
        test($(TextField.class).withCaption("Street").first())
                .setValue("Ruukkikatu 2-4");
        test($(TextField.class).withCaption("Postal Code").first())
                .setValue("20540");
        test($(TextField.class).withCaption("City").first()).setValue("Turku");
        test($(TextField.class).withCaption("State").first()).setValue("-");
        test($(TextField.class).withCaption("Country").first())
                .setValue("Finland");

        // Click save button
        $(Button.class).withCaption("Save").first().click();

        // Notification will appear
        Notification notification = $(Notification.class).first();
        assertEquals("'Ruukkikatu 2-4:20540:Turku:-:Finland' stored.",
                test(notification).getText());

        // Assert that form is empty
        assertFormIsEmpty();

        // Filter the item from the Grid 
        test($(TextField.class).id("filter")).setValue("ruukki");
        Grid<SampleAddress> grid = $(Grid.class).first();
        GridTester grid_ = test(grid);

        // Assert Grid row content is the same we entered
        assertEquals("Ruukkikatu 2-4", grid_.getCellText(0, 0));
        assertEquals("20540", grid_.getCellText(0, 1));
        assertEquals("Turku", grid_.getCellText(0, 2));
        assertEquals("-", grid_.getCellText(0, 3));
        assertEquals("Finland", grid_.getCellText(0, 4));

        // Select the item
        grid_.select(0);

        // Assert that form is correctly populated
        assertEquals("Ruukkikatu 2-4",
                $(TextField.class).withCaption("Street").first().getValue());
        assertEquals("20540", $(TextField.class).withCaption("Postal Code")
                .first().getValue());
        assertEquals("Turku",
                $(TextField.class).withCaption("City").first().getValue());
        assertEquals("-",
                $(TextField.class).withCaption("State").first().getValue());
        assertEquals("Finland",
                $(TextField.class).withCaption("Country").first().getValue());

        // Click to delete
        $(Button.class).withCaption("Delete").first().click();
        notification = $(Notification.class).last();
        assertEquals("Deleted.", test(notification).getText());
        // Assert that form is empty
        assertFormIsEmpty();

        assertEquals(0,grid_.size());
    }

    private void assertFormIsEmpty() {
        assertEquals("",
                $(TextField.class).withCaption("Street").first().getValue());
        assertEquals("", $(TextField.class).withCaption("Postal Code").first()
                .getValue());
        assertEquals("",
                $(TextField.class).withCaption("City").first().getValue());
        assertEquals("",
                $(TextField.class).withCaption("State").first().getValue());
        assertEquals("",
                $(TextField.class).withCaption("Country").first().getValue());

    }

}
