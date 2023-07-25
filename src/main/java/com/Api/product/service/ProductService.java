package com.Api.product.service;

import com.Api.product.cache.ProductCache;
import com.Api.product.dao.ProductDaoImplementation;
import com.Api.product.dto.request.AddRequestDto;
import com.Api.product.dto.response.ResponseDto;
import com.Api.product.dto.response.ResponseListDto;
import com.Api.product.model.ProductEntity;
import com.Api.product.util.Validate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
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

    @Autowired
    Validate validate;

    @Autowired
    ProductCache productCache;
    final org.slf4j.Logger logger= LoggerFactory.getLogger(ProductDaoImplementation.class);

    @Autowired
    private KafkaTemplate<String, ProductEntity> kafkaTemplate;

    @Autowired
    public static final String TOPIC ="Products";
    public ResponseEntity<ResponseListDto> getAllProducts(int pageNumber) {

        try{
            // if pageNumber is less than or zero, its invalid input
            if(pageNumber<=0){
                responseListDto.setMessage("Invalid Input");
                responseListDto.setStatus(false);
                responseListDto.setData(null);
                logger.error("Invalid Input for page number");
                return new ResponseEntity<>(responseListDto,HttpStatus.BAD_GATEWAY);
            }

            // else get result from database
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
        ProductEntity productEntity;
        productEntity = productCache.getProductById(id);


        if(productEntity!=null) {
            responseDto.setMessage("Found in cache");
            responseDto.setStatus(true);
            responseDto.setData(productEntity);
        }

        else {
            // if not present in cache, search in database
            productEntity = productDaoImplementation.getProductById(id);

            // if not found in database as well, ask for valid id
            if (productEntity == null) {
                responseDto.setMessage("Please enter a valid Id");
                logger.error("Error, Invalid Id");
                responseDto.setStatus(false);
                responseDto.setData(null);
                return responseDto;
            } else {
                responseDto.setMessage("Found in Database");
                logger.info("Product with Id: " + id + " listed");
                responseDto.setStatus(true);
                responseDto.setData(productEntity);
                productCache.putProductEntityToCache(productEntity);
            }
        }
        return responseDto;
    }
    public ResponseListDto getEnabledProducts(boolean enable){
        // list all the products based on enable or disabled
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

    public ResponseDto addOrUpdateProduct(ProductEntity productEntity) {
        try {
            Optional<ProductEntity> productEntityOptional = productDaoImplementation.getProductByCode(productEntity.getSkuCode());

            if (productEntityOptional.isPresent()) { // if product is already present update it
                ProductEntity product = productEntityOptional.get();

                // if client id or id is changed throw error
                if(product.getClientId()!=productEntity.getClientId()) throw new Exception("client id cannot be updated");
                if(product.getId()!=productEntity.getId()) throw new Exception("id cannot be updated");

                // else update product
                product.setName(productEntity.getName());
                product.setEnable(productEntity.getEnable());
                product.setLastModifiedDate(LocalDate.now());
                productDaoImplementation.addProduct(product);

                responseDto.setMessage("Product Updated");
                responseDto.setData(productEntity);
                responseDto.setStatus(true);

                // update in cache
                productCache.putProductEntityToCache(productEntity);
                logger.info("Product Updated!");
                return  responseDto;
            }

            // else add the new product
            else {

                if( productEntity.getId()!=0) throw new Exception("Do not enter Id, it is auto generated");
                // if clientId and skuCode are unique, only then add product
                if(validate.checkUniqueClientId(productEntity.getClientId()) &&
                        validate.checkUniqueSkuCode(productEntity.getSkuCode())
                ){
                    productDaoImplementation.addProduct(productEntity);

                    responseDto.setMessage("Product Added");
                    responseDto.setData(productEntity);
                    responseDto.setStatus(true);

                    // add in cache
                    productCache.putProductEntityToCache(productEntity);
                    logger.info("Product Added!");
                }
                // if clientId or skuCode are not unique, throw error
                else{
                    responseDto.setData(null);
                    responseDto.setStatus(false);
                    if(!validate.checkUniqueSkuCode(productEntity.getSkuCode()))
                        responseDto.setMessage("Enter unique sku code");

                    else if(!validate.checkUniqueId(productEntity.getId()))
                        responseDto.setMessage("Enter Unique Id");
                    else if(!validate.checkUniqueClientId(productEntity.getClientId()))
                        responseDto.setMessage("Enter Unique client id");
                }
                return responseDto;
            }
        }

        // if an exception occurs, log it and return response
        catch (Exception e) {
            responseDto.setStatus(false);
            responseDto.setData(null);
            responseDto.setMessage(e.getMessage());
            logger.error("Failed to  add product. "+ HttpStatus.BAD_REQUEST);
            return responseDto;
        }
    }

    public ResponseDto addOrUpdateProductsByKafka(ProductEntity productEntity) {
        try {
            Optional<ProductEntity> productEntityOptional = productDaoImplementation.getProductByCode(productEntity.getSkuCode());

            if (productEntityOptional.isPresent()) { // if product is already present update it
                ProductEntity product = productEntityOptional.get();

                // if client id or id is changed throw error
                if(product.getClientId()!=productEntity.getClientId()) throw new Exception("client id cannot be updated");
                if(product.getId()!=productEntity.getId()) throw new Exception("id cannot be updated");

                // else update product
                product.setName(productEntity.getName());
                product.setEnable(productEntity.getEnable());
                product.setLastModifiedDate(LocalDate.now());
                productDaoImplementation.addProduct(product);

                // update in cache
                productCache.putProductEntityToCache(productEntity);

                responseDto.setMessage("Product Updated");
                responseDto.setData(productEntity);
                responseDto.setStatus(true);

                logger.info("Product Updated!");
                return  responseDto;
            }

            // else add the new product
            else {

                if( productEntity.getId()!=0) throw new Exception("Do not enter Id, it is auto generated");

                // if clientId and skuCode are unique, only then add product
                if(validate.checkUniqueClientId(productEntity.getClientId()) &&
                        validate.checkUniqueSkuCode(productEntity.getSkuCode())
                ){
                    responseDto.setMessage("Product Added");
                    responseDto.setData(productEntity);
                    responseDto.setStatus(true);
                    kafkaTemplate.send("Product", productEntity);
                    // add to cache
                    productCache.putProductEntityToCache(productEntity);
                    logger.info("Product Added!");
                }
                // if clientId or skuCode are not unique, throw error
                else{
                    responseDto.setData(null);
                    responseDto.setStatus(false);
                    if(!validate.checkUniqueSkuCode(productEntity.getSkuCode()))
                        responseDto.setMessage("Enter unique sku code");

                    else if(!validate.checkUniqueId(productEntity.getId()))
                        responseDto.setMessage("Enter Unique Id");
                    else if(!validate.checkUniqueClientId(productEntity.getClientId()))
                        responseDto.setMessage("Enter Unique client id");
                }
                return responseDto;
            }
        }

        // if an exception occurs, log it and return response
        catch (Exception e) {
            responseDto.setStatus(false);
            responseDto.setData(null);
            responseDto.setMessage(e.getMessage());
            logger.error("Failed to  add product. "+ HttpStatus.BAD_REQUEST);
            return responseDto;
        }
    }

    public void saveAddedProduct(ProductEntity productEntity){
        try{
            productDaoImplementation.addProduct(productEntity);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            return;
        }
    }
}