package com.example.application.data.service;

import com.example.application.data.entity.SampleAddress;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleAddressRepository extends JpaRepository<SampleAddress, UUID> {

    Page<SampleAddress> findAllByStreetContainsIgnoreCase(String street, Pageable pageable);
    
    int countByStreetContainsIgnoreCase(String street);
}