package com.Api.product.controller;

import com.Api.product.dto.response.ResponseDto;
import com.Api.product.dto.response.ResponseListDto;
import com.Api.product.model.ProductEntity;
import com.Api.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/allProducts")
    public ResponseListDto getAllProducts() {
        return productService.getAllProducts();
    }
    @GetMapping("/productById/{id}")
    public ResponseDto getProductById(@PathVariable int id) {
        return productService.getProductById(id);
    }
    @PostMapping("/addProductsOrUpdate")
    public ResponseEntity<String> addOrUpdateProduct(@RequestBody ProductEntity productEntity) {
        return productService.addOrUpdateProduct(productEntity);
    }

    @GetMapping("/enable/{enable}")
    public ResponseListDto getEnabledProducts(@PathVariable boolean enable){
        return productService.getEnabledProducts(enable);
    }

}
