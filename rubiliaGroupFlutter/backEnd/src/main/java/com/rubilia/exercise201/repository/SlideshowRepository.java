package com.rubilia.exercise201.repository;

import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rubilia.exercise201.entity.Slideshow;

@Repository
public interface SlideshowRepository extends JpaRepository<Slideshow, UUID> {
    List<Slideshow> findByPublishedTrueOrderByDisplayOrderAsc();

    List<Slideshow> findByOrderByDisplayOrderAsc();
}