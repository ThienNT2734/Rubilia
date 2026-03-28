package com.rubilia.exercise201.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.entity.Tag;
import com.rubilia.exercise201.repository.TagRepository;
import com.rubilia.exercise201.service.TagService;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    public Optional<Tag> findById(UUID id) {
        return tagRepository.findById(id);
    }

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public void deleteById(UUID id) {
        tagRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return tagRepository.existsById(id);
    }

    @Override
    public boolean existsByTagName(String tagName) {
        return tagRepository.existsByTagName(tagName);
    }

    @Override
    public void addProductToTag(Tag tag, Product product) {
        // Logic to add product to tag (if there's a many-to-many relationship)
        tagRepository.save(tag);
    }

    @Override
    public void removeProductFromTag(Tag tag, Product product) {
        // Logic to remove product from tag (if there's a many-to-many relationship)
        tagRepository.save(tag);
    }
}