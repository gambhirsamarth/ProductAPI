package com.Api.product.service;

import com.Api.product.dao.ProductDaoImplementation;
import com.Api.product.dto.request.AddRequestDto;
import com.Api.product.dto.response.ResponseDto;
import com.Api.product.dto.response.ResponseListDto;
import com.Api.product.model.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class ProductService {
    @Autowired
    private ProductDaoImplementation productDaoImplementation;
    @Autowired
    AddRequestDto addRequestDto;
    @Autowired
    ResponseDto responseDto;
    @Autowired
    ResponseListDto responseListDto;

    public ResponseListDto getAllProducts() {
        List<ProductEntity> data = new ArrayList<>();
        int pageNo =1;
        int pageSize = 5;
        try{
            do {
                List<ProductEntity> values=productDaoImplementation.getAllProducts(pageNo, pageSize);
                data.addAll(values);
                if(values.size()!=pageSize){
                    break;
                }
                pageNo+=1;
            }
            while (true);
            responseListDto.setData(data);
            responseListDto.setMessage("Success");
            responseListDto.setStatus(true);
            return responseListDto;
        }
        catch (Exception e){
            responseListDto.setStatus(false);
            responseListDto.setData(null);
            responseListDto.setMessage("Values can't be added");
            return responseListDto;
        }

    }

    public ResponseDto getProductById(int id) {
        ProductEntity productEntity = productDaoImplementation.getProductById(id);
        if(productEntity==null){
            responseDto.setMessage("Invalid id");
            responseDto.setStatus(false);
            responseDto.setData(null);
            return responseDto;
        }
        else {
            responseDto.setMessage("Success");
            responseDto.setStatus(true);
            responseDto.setData(productEntity);
            return responseDto;
        }
    }

    public ResponseEntity<String> addOrUpdateProduct(ProductEntity productEntity) {
        try {
            Optional<ProductEntity> productEntityOptional = productDaoImplementation.getProductByCode(productEntity.getSkuCode());
            if (productEntityOptional.isPresent()) {
                ProductEntity product = productEntityOptional.get();
                product.setClientId(productEntity.getClientId());
                product.setName(productEntity.getName());
                product.setEnable(productEntity.getEnable());
                product.setLastModifiedDate(LocalDate.now());

                productDaoImplementation.addProduct(productEntity);
                return new ResponseEntity<>(productEntity.toString(), HttpStatus.ACCEPTED);
            }
            productDaoImplementation.addProduct(productEntity);

            return new ResponseEntity<>(productEntity.toString(), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add product: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseListDto getEnabledProducts(boolean enable){
        List<ProductEntity> productEntity = productDaoImplementation.getEnabledProducts(enable);

        if(productEntity==null){
            responseListDto.setMessage("Invalid Status");
            responseListDto.setStatus(false);
            responseListDto.setData(productEntity);
        }
        else{
            responseListDto.setMessage("Success");
            responseListDto.setStatus(true);
            responseListDto.setData(productEntity);
        }
        return responseListDto;
    }
}

