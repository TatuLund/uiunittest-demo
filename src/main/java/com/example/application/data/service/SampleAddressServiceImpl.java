package com.example.application.data.service;

import com.example.application.data.entity.SampleAddress;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SampleAddressServiceImpl implements SampleAddressService {

    private final SampleAddressRepository repository;

    @Autowired
    public SampleAddressServiceImpl(SampleAddressRepository repository) {
        this.repository = repository;
    }

    public Optional<SampleAddress> get(UUID id) {
        return repository.findById(id);
    }

    public SampleAddress update(SampleAddress entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<SampleAddress> list(Pageable pageable, Optional<String> filter) {
        if (filter.isPresent()) {
            return repository.findAllByStreetContainsIgnoreCase((String) filter.get(), pageable);
        } else {
            return repository.findAll(pageable);
        }
    }

    public int count(Optional<String> filter) {
        if (filter.isPresent()) {
            return repository.countByStreetContainsIgnoreCase(filter.get());
        } else {
            return (int) repository.count();
        }
    }

}
