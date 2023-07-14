package com.Api.product.service;

import com.Api.product.dao.ProductDaoImplementation;
import com.Api.product.dto.request.AddRequestDto;
import com.Api.product.dto.response.ResponseDto;
import com.Api.product.dto.response.ResponseListDto;
import com.Api.product.model.ProductEntity;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

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
    final org.slf4j.Logger logger= LoggerFactory.getLogger(ProductDaoImplementation.class);
    public ResponseEntity<ResponseListDto> getAllProducts(int pageNumber) {
        List<ProductEntity> data = new ArrayList<>();
        int pageSize = 20;
        try{
            if(pageNumber<=0){
                responseListDto.setMessage("Invalid Input");
                responseListDto.setStatus(false);
                responseListDto.setData(null);
                logger.error("Invalid Input for page number");
                return new ResponseEntity<>(responseListDto,HttpStatus.BAD_GATEWAY);
            }
            else {
                List<ProductEntity> values = productDaoImplementation.getAllProducts(pageNumber);
                if(values!=null && values.size()>0){
                    responseListDto.setMessage("Got all the products in the table");
                    responseListDto.setStatus(true);
                    responseListDto.setData(values);
                    logger.info("Got All the products in the table");
                    return new ResponseEntity<>(responseListDto, HttpStatus.ACCEPTED);
                }
                else{
                    responseListDto.setStatus(false);
                    responseListDto.setData(null);
                    responseListDto.setMessage("Empty Records for products");
                    logger.info("Empty record for products");
                    return new ResponseEntity<>(responseListDto,HttpStatus.BAD_GATEWAY);
                }
            }
        }
        catch(Exception e){
            responseListDto.setStatus(false);
            responseListDto.setMessage("Values could not be found");
            responseListDto.setData(null);
            logger.error("Values could not be found");
            return new ResponseEntity<>(responseListDto,HttpStatus.BAD_GATEWAY);
        }
    }
    public ResponseDto getProductById(int id) {
        ProductEntity productEntity = productDaoImplementation.getProductById(id);
        if(productEntity==null){
            responseDto.setMessage("Please enter a valid Id");
            logger.error("Error, Invalid Id");
            responseDto.setStatus(false);
            responseDto.setData(null);
            return responseDto;
        }
        else {
            responseDto.setMessage("Found the product with given Id");
            logger.info("Product with Id: " + id + " listed");
            responseDto.setStatus(true);
            responseDto.setData(productEntity);
            return responseDto;
        }
    }
    public ResponseDto addOrUpdateProduct(ProductEntity productEntity) {
        try {
            Optional<ProductEntity> productEntityOptional = productDaoImplementation.getProductByCode(productEntity.getSkuCode());
            if (productEntityOptional.isPresent()) { // if product is already present update it
                ProductEntity product = productEntityOptional.get();
                product.setClientId(productEntity.getClientId());
                product.setName(productEntity.getName());
                product.setEnable(productEntity.getEnable());
                product.setLastModifiedDate(LocalDate.now());
                productDaoImplementation.addProduct(productEntity);
                responseDto.setMessage("Product Updated");
                responseDto.setData(productEntity);
                responseDto.setStatus(true);
                logger.info("Product Updated!");
                return  responseDto;
            }
            // else add the new product
            productDaoImplementation.addProduct(productEntity);
            responseDto.setMessage("Product Added");
            responseDto.setData(productEntity);
            responseDto.setStatus(true);
            logger.info("Product Added!");
            return  responseDto;
        }
        // if an exception occurs, log it and return response
        catch (Exception e) {
            responseDto.setStatus(false);
            responseDto.setData(null);
            responseDto.setMessage("Failed to add product");
            logger.error("Failed to  add product. "+ HttpStatus.BAD_REQUEST);
            return responseDto;
        }
    }
    // list all the products based on enable or disabled
    public ResponseListDto getEnabledProducts(boolean enable){
        List<ProductEntity> productEntity = productDaoImplementation.getEnabledProducts(enable);

        if(productEntity==null){
            responseListDto.setMessage("Please enter a valid value");
            responseListDto.setStatus(false);
            responseListDto.setData(productEntity);
            logger.error("Invalid Status");
        }
        else{
            responseListDto.setMessage("Products listed");
            responseListDto.setStatus(true);
            responseListDto.setData(productEntity);
            logger.info("Product with enable = "+enable+ " listed");
        }
        return responseListDto;
    }
}