package com.Api.product.controller;

import com.Api.product.dto.response.ResponseDto;
import com.Api.product.dto.response.ResponseListDto;
import com.Api.product.model.ProductEntity;
import com.Api.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/products/v1")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/get-all-products") // get all the products
    public ResponseEntity<ResponseListDto> getAllProducts(@RequestParam(name = "pageNo") int pageNo) {
        return productService.getAllProducts(pageNo);
    }

    @GetMapping("/get-product-by-id") // get product by a specific id
    public ResponseDto getProductById(@RequestParam(name = "id") int id) {
        return productService.getProductById(id);
    }

    @PostMapping("/add-update-product") // adds product if not already present or updates existing product
    public ResponseDto addOrUpdateProduct(@RequestBody ProductEntity productEntity) {
        return productService.addOrUpdateProduct(productEntity);
    }
    @GetMapping("/enable") // lists all the product by checking if its enable or disabled
    public ResponseListDto getEnabledProducts(@RequestParam(name = "status") boolean enable) {
        return productService.getEnabledProducts(enable);
    }
}
