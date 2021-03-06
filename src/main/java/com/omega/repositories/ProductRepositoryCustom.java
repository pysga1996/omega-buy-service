package com.omega.repositories;

import com.omega.model.entity.Product;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ProductRepositoryCustom {

    @NonNull
    List<Product> findAllFetchManufacturer();
}
