package com.Api.product.util;

import com.Api.product.model.ProductEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;
@Component
public class Validate {
    @Autowired
    private SessionFactory sessionFactory;

    public boolean checkUniqueId(int id) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("FROM ProductEntity WHERE id = :id");
        query.setParameter("id", id);
        List<ProductEntity> products = query.getResultList();
        if(products.isEmpty()) return true;
        return false;
    }
    public boolean checkUniqueClientId(int clientId) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("FROM ProductEntity WHERE client_id = :clientId");
        query.setParameter("clientId", clientId);
        List<ProductEntity> products = query.getResultList();
        if(products.isEmpty()) return true;
        return false;
    }
    public boolean checkUniqueSkuCode(int skuCode) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("FROM ProductEntity WHERE sku_code = :skuCode");
        query.setParameter("skuCode", skuCode);
        List<ProductEntity> products = query.getResultList();
        if(products.isEmpty()) return true;
        return false;
    }
}
