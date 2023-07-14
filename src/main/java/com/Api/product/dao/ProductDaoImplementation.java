package com.Api.product.dao;

import com.Api.product.config.HibernateConfiguration;
import com.Api.product.model.ProductEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDaoImplementation {
    HibernateConfiguration hibernateConfiguration;
    @Autowired
    SessionFactory sessionFactory;
    public final static org.slf4j.Logger logger= LoggerFactory.getLogger(ProductDaoImplementation.class);
    public List<ProductEntity> getAllProducts(int pageNo) {
        // get all the products using pagination
        Session session = sessionFactory.openSession();
        int pageSize = 20;
        int offset=(pageNo-1)*pageSize;  // the number of records to be skipped for returning data of current page
        Query query = session.createQuery("FROM ProductEntity").setFirstResult(offset).setMaxResults(pageSize);
        return query.getResultList();
    }
    public ProductEntity getProductById(int id) {
        EntityManager entityManager = sessionFactory.createEntityManager();
        try {
            TypedQuery<ProductEntity> query = entityManager.createQuery("SELECT t FROM ProductEntity t WHERE t.id = :id", ProductEntity.class);
            query.setParameter("id", id);
            List<ProductEntity> resultList = query.getResultList();

            if (resultList.isEmpty()) {
                logger.info("No product Found");
                return null;
            }
            return resultList.get(0);
        }
        catch (Exception e){
            logger.info(e.getMessage());
            throw new IllegalStateException("No product Found");
        }
        finally {
            entityManager.close();
        }
    }
    public void addProduct(ProductEntity productEntity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(productEntity);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }
    public Optional<ProductEntity> getProductByCode(int skuCode) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("FROM ProductEntity WHERE skuCode = :skuCode");
        query.setParameter("skuCode", skuCode);
        List<ProductEntity> products = query.getResultList();
        return products.stream().findFirst();
    }
    public List<ProductEntity> getEnabledProducts(boolean enable) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("FROM ProductEntity WHERE enable = :enable");
        query.setParameter("enable", enable);
        List<ProductEntity> products = query.getResultList();
        return products;
    }
}