package com.rubilia.exercise201.repository;

import com.rubilia.exercise201.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    boolean existsByTagName(String tagName);
}