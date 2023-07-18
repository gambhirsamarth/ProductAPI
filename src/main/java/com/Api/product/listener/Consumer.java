package com.Api.product.listener;

import com.Api.product.model.ProductEntity;
import com.Api.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
    @Autowired
    ProductService productService;

    public static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(
            topics = "Product",
            groupId = "API",
            containerFactory = "userKafkaListenerFactory"
    )
    public void consumer(ProductEntity productEntity){
        logger.info("Consumed Successfully");
        productService.saveAddedProduct(productEntity);
    }
}
