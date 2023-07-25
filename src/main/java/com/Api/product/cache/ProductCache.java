package com.Api.product.cache;

import com.Api.product.model.ProductEntity;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductCache {
    @Autowired
    RedissonClient client = Redisson.create();

    private static final String PRODUCT_ID_TO_PRODUCT_MAP = "PRODUCT_ID_TO_PRODUCT_MAP";
    private static final String PAGE_NUMBER_TO_PRODUCTS_MAP = "PAGE_NUMBER_TO_PRODUCTS_MAP";

    public ProductEntity getProductById(int id){
        RMap<String, ProductEntity> map = client.getMap(PRODUCT_ID_TO_PRODUCT_MAP);
        ProductEntity product = null;

        if(map.containsKey(String.valueOf(id)))
            product = map.get(String.valueOf(id));

        return product;
    }
    public void putProductEntityToCache(ProductEntity product){
        RMap<String, ProductEntity> map = client.getMap(PRODUCT_ID_TO_PRODUCT_MAP);

        // if already exists in the cache, update its value
        if(map.containsKey(String.valueOf(product.getId()))){
            map.replace(String.valueOf(product.getId()), product);
        }
        // else add into the cache
        else {
            map.put(String.valueOf(product.getId()), product);
        }
    }
}