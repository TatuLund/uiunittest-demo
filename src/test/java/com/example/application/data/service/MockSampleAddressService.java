package com.example.application.data.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public Page<SampleAddress> list(Pageable pageable) {
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        addresses.stream().skip(offset);
        int limit = Math.min(addresses.size(), offset + pageable.getPageSize());
        return new PageImpl<>(
                addresses.subList(offset, limit));
    }

    @Override
    public int count() {
        return addresses.size();
    }

}
