package com.javixtc.products_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javixtc.products_service.model.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
