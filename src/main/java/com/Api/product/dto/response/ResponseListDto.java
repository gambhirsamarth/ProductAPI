package com.Api.product.dto.response;

import com.Api.product.model.ProductEntity;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Data
public class ResponseListDto {
    List<ProductEntity> data;
    boolean status;
    String message;

}
