package com.example.application.data.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.application.data.entity.SampleAddress;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;

public class MockSampleAddressService implements SampleAddressService {

    List<SampleAddress> addresses = new ArrayList<>();

    public MockSampleAddressService() {
        var addressesGenerator = new ExampleDataGenerator<>(SampleAddress.class,
                LocalDateTime.now());
        addressesGenerator.setData(SampleAddress::setStreet, DataType.ADDRESS);
        addressesGenerator.setData(SampleAddress::setPostalCode,
                DataType.ZIP_CODE);
        addressesGenerator.setData(SampleAddress::setState, DataType.STATE);
        addressesGenerator.setData(SampleAddress::setCountry, DataType.COUNTRY);
        addressesGenerator.setData(SampleAddress::setCity, DataType.CITY);
        addressesGenerator.setData(SampleAddress::setId, DataType.UUID);
        addresses = addressesGenerator.create(500, 123);
    }

    @Override
    public Optional<SampleAddress> get(UUID id) {
        return addresses.stream().filter(address -> address.getId().equals(id))
                .findFirst();
    }

    @Override
    public SampleAddress update(SampleAddress entity) {
        if (entity.getId() != null) {
            get(entity.getId()).ifPresent(address -> addresses.remove(address));
        }
        UUID id = UUID.randomUUID();
        entity.setId(id);
        addresses.add(entity);
        return entity;
    }

    @Override
    public void delete(UUID id) {
        get(id).ifPresent(address -> {
            addresses.remove(address);
        });
    }

    @Override
    public Page<SampleAddress> list(Pageable pageable,
            Optional<String> filter) {
        // Note: Sorting not implemented as our test case is not requiring it.
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        List<SampleAddress> filtered = doFilter(filter);
        int limit = Math.min(filtered.size(), offset + pageable.getPageSize());
        return new PageImpl<>(filtered.subList(offset, limit));
    }

    private List<SampleAddress> doFilter(Optional<String> filter) {
        return addresses.stream().filter(address -> {
            if (filter.isPresent()) {
                return address.getStreet().toLowerCase()
                        .contains(filter.get().toLowerCase());
            } else {
                return true;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public int count(Optional<String> filter) {
        return doFilter(filter).size();
    }

}
