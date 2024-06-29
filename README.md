# Restaurant
The Restaurant Management System is designed to facilitate the management of a restaurant's operations including products (menu items) and tables.
The system provides RESTful APIs to manage and interact with products and tables. It includes various modules and components as outlined below.

Technologies Used
.Java
.Spring Boot
.Hibernate
.JUnit
.Docker
.Mockito
.Json

## Overview
The combination of these technologies ensures that the Restaurant Management System is robust, scalable, and easy to maintain.
Leveraging Java and Spring Boot provides a solid foundation for the application, while Hibernate simplifies data management.
Testing with JUnit and Mockito ensures high code quality, and Docker facilitates smooth deployment and integration processes. 
Using JSON for data interchange aligns with modern web service standards, ensuring interoperability and ease of use.

### Project Structure
.controller: Handles HTTP requests and serves as the entry point for various operations.
.dto: Data Transfer Objects for transferring data between layers.
.entity: Entity classes representing database tables.
.exceptions: Custom exception classes for handling specific error scenarios.
.repository: Interfaces for database interaction using Spring Data JPA.
.service: Business logic implementations for handling warehouse operations.
.resources: Configuration files and static resources.
.test: Contains unit and integration tests for ensuring the reliability of the application.

##### Setup

1. Clone this repo
```
git clone https://github.com/SzymonKaczmarek96/WMS.git
```
2.Build the project using Maven
```
mvn clean install
```
```
mvn spring-boot:run
```

3.Docker Setup:
Create and start the Docker containers
```
docker-compose up
```
docker-compose configuartion
```
version: '3.8'

services:
  postgres:
    image: postgres:15
    ports:
      - "5432:5432"
    volumes:
      - db_data:/var/lib/postgresql/data
    environment:
      POSTGRES_PASSWORD: user
      POSTGRES_USER: postgres
      POSTGRES_DB: warehouse
volumes:
  db_data:
```

###### Endpoints

|HTTP Method|URL|Description|
|---|---|---|
|PRODUCT|
|`GET`|http://localhost:8000/product | Get list of products |
|`GET`|http://localhost:8000/product/{productName}| Get Product by product name |
|`POST`|http://localhost:8000/product/create | Create product by request body |
|`PUT`|http://localhost:8000/product/{productName}/update | Update product by request body |
|`DELETE`|http://localhost:8000/product/{productName}/delete/ | Delete product by productName |
|TABLE|
|`GET`|http://localhost:8000/table | Get list of table |
|`GET`|http://localhost:8000/table/check/?status={TableStatus}|Get list of all tables with entered status|
|`POST`|http://localhost:8000/table/create | Create new table |
|`PUT`|http://localhost:8000/table/{id}/open | Change table status from "PAID" to "FREE" |
|`PUT`|http://localhost:8000/table/{id}/close | Change table status from "OCCUPEID_WITH_PRODUCTS" to "OCCUPEID" |
|`PUT`|http://localhost:8000/table/{id}/reservation | Change table status from "FREE" to "OCCUPEID" |
|`PUT`|http://localhost:8000/table/{id}/cancel | Change table status from "OCCUPEID" to "FREE" |
|`PUT`|http://localhost:8000/table/{id}/order | Add product to products on table |
|`PUT`|http://localhost:8000/table/{id}/delete | Delete product from products on table |

