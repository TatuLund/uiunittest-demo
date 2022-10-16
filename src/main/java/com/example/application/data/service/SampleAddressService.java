package com.example.application.data.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.application.data.entity.SampleAddress;

public interface SampleAddressService {

    public Optional<SampleAddress> get(UUID id);

    public SampleAddress update(SampleAddress entity);

    public void delete(UUID id);

    public Page<SampleAddress> list(Pageable pageable, Optional<String> filter);

    public int count(Optional<String> filter);
}
