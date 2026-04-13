package com.ecomm.web.repository;

import com.ecomm.web.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIgnoreCaseOrderByIdAsc(String category);

    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByIdAsc(String name, String description);

    List<Product> findByCategoryIgnoreCaseAndNameContainingIgnoreCaseOrCategoryIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByIdAsc(
            String categoryForName,
            String name,
            String categoryForDescription,
            String description
    );
}
