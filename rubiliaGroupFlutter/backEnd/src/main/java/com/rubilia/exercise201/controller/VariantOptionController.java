package com.rubilia.exercise201.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rubilia.exercise201.dto.VariantOptionDTO;
import com.rubilia.exercise201.entity.Gallery;
import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.entity.VariantOption;
import com.rubilia.exercise201.service.ProductService;
import com.rubilia.exercise201.service.VariantOptionService;

@RestController
@RequestMapping("/api/variant-options")
public class VariantOptionController {

    @Autowired
    private VariantOptionService variantOptionService;

    @Autowired
    private ProductService productService;

    private VariantOptionDTO convertToDTO(VariantOption variantOption) {
        VariantOptionDTO dto = new VariantOptionDTO();
        dto.setId(variantOption.getId());
        dto.setTitle(variantOption.getTitle());
        dto.setImageId(null); // TODO: fix if needed
        dto.setPrice(variantOption.getPrice());
        dto.setBuyingPrice(variantOption.getBuyingPrice());
        dto.setQuantity(variantOption.getQuantity());
        dto.setSku(variantOption.getSku());
        dto.setActive(variantOption.getActive());
        return dto;
    }

    @GetMapping
    public ResponseEntity<List<VariantOptionDTO>> getAllVariantOptions() {
        List<VariantOption> variantOptions = variantOptionService.findAll();
        List<VariantOptionDTO> dtoList = variantOptions.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VariantOption> getVariantOptionById(@PathVariable UUID id) {
        return variantOptionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<VariantOption>> getVariantOptionsByProduct(
            @PathVariable UUID productId) {
        return productService.getProductById(productId)
                .map(product -> ResponseEntity.ok(variantOptionService.findByProduct(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}/active")
    public ResponseEntity<List<VariantOption>> getActiveVariantOptionsByProduct(
            @PathVariable UUID productId) {
        return productService.getProductById(productId)
                .map(product -> ResponseEntity.ok(variantOptionService.findByProductAndActiveTrue(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VariantOption> createVariantOption(@RequestBody VariantOption variantOption) {
        return ResponseEntity.ok(variantOptionService.save(variantOption));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VariantOption> updateVariantOption(
            @PathVariable UUID id,
            @RequestBody VariantOption variantOption) {
        if (!variantOptionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        variantOption.setId(id);
        return ResponseEntity.ok(variantOptionService.save(variantOption));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVariantOption(@PathVariable UUID id) {
        if (!variantOptionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        variantOptionService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Void> updateVariantImage(
            @PathVariable UUID id,
            @RequestBody Gallery image) {
        VariantOption variantOption = variantOptionService.findById(id).orElse(null);
        if (variantOption == null) {
            return ResponseEntity.notFound().build();
        }
        variantOptionService.updateVariantImage(variantOption, image);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<Void> toggleActive(@PathVariable UUID id) {
        if (!variantOptionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        variantOptionService.toggleActive(id);
        return ResponseEntity.ok().build();
    }
}