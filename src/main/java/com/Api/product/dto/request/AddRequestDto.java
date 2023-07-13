package com.Api.product.dto.request;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AddRequestDto {

    private int clientId;

    private int skuCode;

    private String name;

    private Boolean enable;
}
