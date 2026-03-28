package com.rubilia.exercise201.repository;

import com.rubilia.exercise201.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, UUID> {
    boolean existsByAttributeName(String attributeName);
}