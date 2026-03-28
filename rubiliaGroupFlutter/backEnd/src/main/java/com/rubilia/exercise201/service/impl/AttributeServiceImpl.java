package com.rubilia.exercise201.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rubilia.exercise201.entity.Attribute;
import com.rubilia.exercise201.entity.AttributeValue;
import com.rubilia.exercise201.repository.AttributeRepository;
import com.rubilia.exercise201.service.AttributeService;

@Service
@Transactional
public class AttributeServiceImpl implements AttributeService {

    @Autowired
    private AttributeRepository attributeRepository;

    @Override
    public List<Attribute> findAll() {
        return attributeRepository.findAll();
    }

    @Override
    public Optional<Attribute> findById(UUID id) {
        return attributeRepository.findById(id);
    }

    @Override
    public Attribute save(Attribute attribute) {
        return attributeRepository.save(attribute);
    }

    @Override
    public void deleteById(UUID id) {
        attributeRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return attributeRepository.existsById(id);
    }

    @Override
    public boolean existsByAttributeName(String attributeName) {
        return attributeRepository.existsByAttributeName(attributeName);
    }

    @Override
    public AttributeValue addAttributeValue(Attribute attribute, String value, String color) {
        AttributeValue attributeValue = new AttributeValue();
        attributeValue.setAttribute(attribute);
        attributeValue.setAttributeValue(value);
        attributeValue.setColor(color);
        attribute.getAttributeValues().add(attributeValue);
        attributeRepository.save(attribute);
        return attributeValue;
    }

    @Override
    public void removeAttributeValue(AttributeValue attributeValue) {
        Attribute attribute = attributeValue.getAttribute();
        attribute.getAttributeValues().remove(attributeValue);
        attributeRepository.save(attribute);
    }
}