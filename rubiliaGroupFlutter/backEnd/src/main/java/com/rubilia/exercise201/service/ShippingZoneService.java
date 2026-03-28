package com.rubilia.exercise201.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.rubilia.exercise201.entity.ShippingZone;

public interface ShippingZoneService {
    List<ShippingZone> findAll();
    Optional<ShippingZone> findById(UUID id);
    ShippingZone save(ShippingZone shippingZone);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    boolean existsByName(String name);
    List<ShippingZone> findByActiveTrue();
    List<ShippingZone> findByFreeShippingTrue();
}