package com.rubilia.exercise201.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.rubilia.exercise201.entity.Sale;
import com.rubilia.exercise201.service.SaleService;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @GetMapping
    public ResponseEntity<List<Sale>> getAllSales() {
        return ResponseEntity.ok(saleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable UUID id) {
        return saleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createSale(@RequestBody Sale sale, @RequestParam UUID staffId) {
        if (sale.getProduct() != null && saleService.existsByProductId(sale.getProduct().getId())) {
            return ResponseEntity.badRequest()
                    .body("Product đã được gán cho một Sale khác");
        }
        return ResponseEntity.ok(saleService.save(sale, staffId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSale(@PathVariable UUID id, @RequestBody Sale sale, @RequestParam UUID staffId) {
        if (!saleService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (sale.getProduct() != null && saleService.existsByProductId(sale.getProduct().getId())) {
            Optional<Sale> existingSale = saleService.findById(id);
            if (existingSale.isPresent() && (existingSale.get().getProduct() == null ||
                    !existingSale.get().getProduct().getId().equals(sale.getProduct().getId()))) {
                return ResponseEntity.badRequest()
                        .body("Product đã được gán cho một Sale khác");
            }
        }
        return ResponseEntity.ok(saleService.update(id, sale, staffId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable UUID id) {
        if (!saleService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        saleService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{saleId}/products/{productId}")
    public ResponseEntity<?> assignProductToSale(@PathVariable UUID saleId, @PathVariable UUID productId) {
        try {
            Sale updatedSale = saleService.assignProductToSale(saleId, productId);
            return ResponseEntity.ok(updatedSale);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{saleId}/products")
    public ResponseEntity<?> removeProductFromSale(@PathVariable UUID saleId) {
        try {
            saleService.removeProductFromSale(saleId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}