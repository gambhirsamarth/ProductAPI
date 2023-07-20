package com.Api.product.cache;

import com.Api.product.model.ProductEntity;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductCache {
    @Autowired
    RedissonClient client = Redisson.create();

    RMap<String, ProductEntity> map = client.getMap("map");

    public ProductEntity getTicketById(int id){
        ProductEntity product = null;

        if(map.containsKey(String.valueOf(id)))
            product = map.get(String.valueOf(id));

        return product;
    }

    public void putProductEntityToCache(ProductEntity product){
        map.put(String.valueOf(product.getId()), product);
    }
}
