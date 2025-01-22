package com.example.application.integrationtest;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.BrowserTest;

public class AddressViewIT extends AbstractViewTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        // Hide dev mode gizmo, it would interfere screenshot tests
        try {
            $("vaadin-dev-tools").first().setProperty("hidden", true);
        } catch (NotFoundException e) {

        }

        login("admin", "admin");
    }

    @BrowserTest
    public void addItemRemoveItem() {

        // Populate form
        $(TextFieldElement.class).id("street").setValue("Ruukkikatu 2-4");
        $(TextFieldElement.class).id("postalcode").setValue("20540");
        $(TextFieldElement.class).id("city").setValue("Turku");
        $(TextFieldElement.class).id("state").setValue("-");
        $(TextFieldElement.class).id("country").setValue("Finland");

        // Click save button
        $(ButtonElement.class).id("save").click();

        // Notification will appear
        NotificationElement notification = $(NotificationElement.class).last();
        Assertions.assertEquals(
                "'Ruukkikatu 2-4:20540:Turku:-:Finland' stored.",
                notification.getText());

        // Assert that form is empty
        assertFormIsEmpty();

        // Filter the item
        $(TextFieldElement.class).id("filter").setValue("ruukki");
        GridElement grid = $(GridElement.class).first();

        // Assert that item is the same as we entered
        Assertions.assertEquals("Ruukkikatu 2-4", grid.getCell(0, 0).getText());
        Assertions.assertEquals("20540", grid.getCell(0, 1).getText());
        Assertions.assertEquals("Turku", grid.getCell(0, 2).getText());
        Assertions.assertEquals("-", grid.getCell(0, 3).getText());
        Assertions.assertEquals("Finland", grid.getCell(0, 4).getText());

        // Select the item
        grid.select(0);

        // Assert that form is correctly populated
        Assertions.assertEquals("Ruukkikatu 2-4",
                $(TextFieldElement.class).id("street").getValue());
        Assertions.assertEquals("20540",
                $(TextFieldElement.class).id("postalcode").getValue());
        Assertions.assertEquals("Turku",
                $(TextFieldElement.class).id("city").getValue());
        Assertions.assertEquals("-",
                $(TextFieldElement.class).id("state").getValue());
        Assertions.assertEquals("Finland",
                $(TextFieldElement.class).id("country").getValue());

        // Click to delete
        $(ButtonElement.class).id("delete").click();
        notification = $(NotificationElement.class).last();
        Assertions.assertEquals("Deleted.", notification.getText());

        // Assert that form is empty
        assertFormIsEmpty();

        Assertions.assertEquals(0, grid.getRowCount());
    }

    private void assertFormIsEmpty() {
        Assertions.assertEquals("",
                $(TextFieldElement.class).id("street").getValue());
        Assertions.assertEquals("",
                $(TextFieldElement.class).id("postalcode").getValue());
        Assertions.assertEquals("",
                $(TextFieldElement.class).id("city").getValue());
        Assertions.assertEquals("",
                $(TextFieldElement.class).id("state").getValue());
        Assertions.assertEquals("",
                $(TextFieldElement.class).id("country").getValue());
    }
}
