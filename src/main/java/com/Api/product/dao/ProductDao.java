package com.Api.product.dao;

import com.Api.product.model.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    public List<ProductEntity> getAllProducts();
    public ProductEntity getProductById(int id);
    public void addProduct(ProductEntity productEntity);

    public List<ProductEntity> getEnabledProducts(boolean enable);
    Optional<ProductEntity> getProductByCode(int skuCode);
}
