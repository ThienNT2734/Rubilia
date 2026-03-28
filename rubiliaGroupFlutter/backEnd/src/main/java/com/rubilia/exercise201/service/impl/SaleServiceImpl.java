package com.rubilia.exercise201.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.entity.Sale;
import com.rubilia.exercise201.entity.StaffAccount;
import com.rubilia.exercise201.repository.ProductRepository;
import com.rubilia.exercise201.repository.SaleRepository;
import com.rubilia.exercise201.repository.StaffAccountRepository;
import com.rubilia.exercise201.service.ProductService;
import com.rubilia.exercise201.service.SaleService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SaleServiceImpl implements SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private StaffAccountRepository staffAccountRepository;

    @Override
    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    @Override
    public Optional<Sale> findById(UUID id) {
        return saleRepository.findById(id);
    }

    @Override
    public Sale save(Sale sale, UUID staffId) {
        StaffAccount staff = staffAccountRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại"));
        sale.setCreatedBy(staff);
        sale.setUpdatedBy(staff);
        return saleRepository.save(sale);
    }

    @Override
    public Sale update(UUID id, Sale sale, UUID staffId) {
        Optional<Sale> existingSale = saleRepository.findById(id);
        if (existingSale.isEmpty()) {
            throw new IllegalArgumentException("Sale không tồn tại");
        }
        StaffAccount staff = staffAccountRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại"));
        sale.setId(id);
        sale.setCreatedBy(existingSale.get().getCreatedBy());
        sale.setUpdatedBy(staff);
        return saleRepository.save(sale);
    }

    @Override
    public void deleteById(UUID id) {
        saleRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return saleRepository.existsById(id);
    }

    @Override
    public boolean existsByProductId(UUID productId) {
        return saleRepository.existsByProductId(productId);
    }

    @Override
    @Transactional
    public Sale assignProductToSale(UUID saleId, UUID productId) {
        Optional<Sale> saleOpt = saleRepository.findById(saleId);
        Optional<Product> productOpt = productService.getProductById(productId);

        if (saleOpt.isEmpty() || productOpt.isEmpty()) {
            throw new IllegalArgumentException("Sale hoặc Product không tồn tại");
        }

        if (saleRepository.existsByProductId(productId)) {
            throw new IllegalStateException("Product đã được gán cho một Sale khác");
        }

        Sale sale = saleOpt.get();
        Product product = productOpt.get();
        sale.setProduct(product);
        return saleRepository.save(sale);
    }

    @Override
    @Transactional
    public void removeProductFromSale(UUID saleId) {
        Optional<Sale> saleOpt = saleRepository.findById(saleId);
        if (saleOpt.isEmpty()) {
            throw new IllegalArgumentException("Sale không tồn tại");
        }

        Sale sale = saleOpt.get();
        sale.setProduct(null);
        saleRepository.save(sale);
    }
}