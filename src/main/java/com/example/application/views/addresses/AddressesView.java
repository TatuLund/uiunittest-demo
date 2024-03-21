package com.example.application.views.addresses;

import com.example.application.data.entity.SampleAddress;
import com.example.application.data.service.SampleAddressService;
import com.example.application.views.MainLayout;
import com.example.application.views.SessionStore;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.security.PermitAll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Addresses")
@Route(value = "addresses/:sampleAddressID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class AddressesView extends Div implements BeforeEnterObserver {

    private final String SAMPLEADDRESS_ID = "sampleAddressID";
    private final String SAMPLEADDRESS_EDIT_ROUTE_TEMPLATE = "addresses/%s/edit";

    private final Grid<SampleAddress> grid = new Grid<>(SampleAddress.class,
            false);

    private TextField street;
    private TextField postalCode;
    private TextField city;
    private TextField state;
    private TextField country;
    private TextField filter = new TextField();

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private final BeanValidationBinder<SampleAddress> binder;
    ConfigurableFilterDataProvider<SampleAddress, Void, String> filteredDataProvider;

    private SampleAddress sampleAddress;

    private final SampleAddressService sampleAddressService;
    SessionStore store;

    @Autowired
    public AddressesView(SampleAddressService sampleAddressService,
            SessionStore store) {
        this.store = store;
        this.sampleAddressService = sampleAddressService;
        addClassNames("addresses-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Form
        binder = new BeanValidationBinder<>(SampleAddress.class);

        // Configure Grid
        grid.addColumn("street").setAutoWidth(true);
        grid.addColumn("postalCode").setAutoWidth(true);
        grid.addColumn("city").setAutoWidth(true);
        grid.addColumn("state").setAutoWidth(true);
        grid.addColumn("country").setAutoWidth(true);
        DataProvider<SampleAddress, String> dataProvider = DataProvider
                .fromFilteringCallbacks(
                        query -> sampleAddressService.list(
                                PageRequest.of(query.getPage(),
                                        query.getPageSize(),
                                        VaadinSpringDataHelpers
                                                .toSpringDataSort(query)),
                                query.getFilter()).stream(),
                        query -> sampleAddressService.count(query.getFilter()));
        filteredDataProvider = dataProvider.withConfigurableFilter();
        grid.setItems(filteredDataProvider);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(
                        String.format(SAMPLEADDRESS_EDIT_ROUTE_TEMPLATE,
                                event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AddressesView.class);
            }
        });

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        filter.addValueChangeListener(event -> {
            filteredDataProvider.setFilter(event.getValue());
        });

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.sampleAddress == null) {
                    this.sampleAddress = new SampleAddress();
                }
                binder.writeBean(this.sampleAddress);
                SampleAddress saved = sampleAddressService
                        .update(this.sampleAddress);
                clearForm();
                refreshGrid();
                Notification.show("'" + saved.toString() + "' stored.");
                UI.getCurrent().navigate(AddressesView.class);
            } catch (ValidationException validationException) {
                Notification.show(
                        "An exception happened while trying to store the sampleAddress details.");
            }
        });

        delete.addClickListener(e -> {
            if (this.sampleAddress != null) {
                sampleAddressService.delete(this.sampleAddress.getId());
                clearForm();
                refreshGrid();
                Notification.show("Deleted.");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> sampleAddressId = null;
        if (event.getRouteParameters() == null || !event.getRouteParameters()
                .get(SAMPLEADDRESS_ID).isPresent()) {
            return;
        }
        try {
            sampleAddressId = event.getRouteParameters().get(SAMPLEADDRESS_ID)
                    .map(UUID::fromString);
        } catch (IllegalArgumentException e) {

        }
        if (sampleAddressId != null && sampleAddressId.isPresent()) {
            Optional<SampleAddress> sampleAddressFromBackend = sampleAddressService
                    .get(sampleAddressId.get());
            if (sampleAddressFromBackend.isPresent()) {
                populateForm(sampleAddressFromBackend.get());
            } else {
                Notification.show(String.format(
                        "The requested sampleAddress was not found, ID = %s",
                        sampleAddressId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(AddressesView.class);
            }
        } else {
            Notification.show(
                    String.format(
                            "The requested sampleAddress id was not valid",
                            event.getRouteParameters().get(SAMPLEADDRESS_ID)),
                    3000, Notification.Position.BOTTOM_START);

        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        street = new TextField("Street");
        street.setId("street");
        postalCode = new TextField("Postal Code");
        postalCode.setId("postalcode");
        city = new TextField("City");
        city.setId("city");
        state = new TextField("State");
        state.setId("state");
        country = new TextField("Country");
        country.setId("country");
        formLayout.add(street, postalCode, city, state, country);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.setId("cancel");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setId("save");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.setId("delete");
        buttonLayout.add(save, delete, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        filter.setPlaceholder("Filter by street");
        filter.setId("filter");
        wrapper.add(filter, grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(SampleAddress value) {
        this.sampleAddress = value;
        binder.readBean(this.sampleAddress);

    }
}
