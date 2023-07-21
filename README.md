
# Product API

The Product API is a backend service that provides Create, Read & Update operations for managing products. It allows clients to interact with the product data in the database. The API uses Spring Boot, Java 8, Maven, MySQL, Hibernate, Kafka & Redis for database interactions.

# API Endpoints

## /products/v1/get-all-products 
Gives all products from the database

## /products/v1/get-product-by-id 
Gives a product with specific id

## /products/v1/add-update-product
Adds a new product or updates if product with given skuCode already exists

## /products/v1/enable
Gives list of all enabled products or disabled products

## /products/v1/add-or-update-products-by-kafka
Adds or updates product using Kafka Server

```
 Pagination is implemented for fetching all products to avoid performance issues with large datasets.
```
