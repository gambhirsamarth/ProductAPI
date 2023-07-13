package com.Api.product.dto.response;

import com.Api.product.model.ProductEntity;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ResponseDto {

    ProductEntity data;
    boolean status;
    String message;
}

