package com.Api.product.model;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "product")

public class ProductEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "client_id", unique = true,nullable = false)
    private int clientId;
    @Column(name = "sku_code", unique = true, nullable = false)
    private int skuCode;
    @Column(name="name", nullable = false)
    private String name;
    @Column(name="last_modified_date", nullable = false)
    private LocalDate lastModifiedDate;

    @Column(name = "enable")
    private Boolean enable;
    @PrePersist
    public void setDefault(){
        this.setLastModifiedDate(LocalDate.now());
    }
}
