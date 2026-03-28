package com.rubilia.exercise201.repository;

import com.rubilia.exercise201.entity.ShippingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShippingZoneRepository extends JpaRepository<ShippingZone, UUID> {
    boolean existsByName(String name);
    List<ShippingZone> findByActiveTrue();
    List<ShippingZone> findByFreeShippingTrue();
}