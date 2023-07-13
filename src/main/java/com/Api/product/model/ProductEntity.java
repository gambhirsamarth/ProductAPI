package com.Api.product.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "product")

public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "client_id")
    private int clientId;
    @Column(name = "sku_code")
    private int skuCode;
    @Column(name="name")
    private String name;
    @Column(name="last_modified_date")
    private LocalDate lastModifiedDate;

    @Column(name = "enable")
    private Boolean enable;
    @PrePersist
    public void setDefault(){
        this.setLastModifiedDate(LocalDate.now());
    }
}
