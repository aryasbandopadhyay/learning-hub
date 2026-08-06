package com.example.crud.service;

import com.example.crud.dto.ProductRequest;
import com.example.crud.dto.ProductResponse;

import java.util.List;

/**
 * ============================================================================================
 * ProductService — the business-logic layer, defined as an INTERFACE.
 * ============================================================================================
 *
 * <h3>Why program to an interface?</h3>
 * This is the <b>Dependency Inversion Principle</b> (the "D" in SOLID) in action: the web layer
 * (controller) depends on this ABSTRACTION, not on a concrete class. Benefits:
 * <ul>
 *   <li>You can swap the implementation (e.g. add caching) without touching the controller.</li>
 *   <li>You can inject a mock implementation in tests.</li>
 * </ul>
 * Spring injects whichever bean implements this interface (here {@code ProductServiceImpl}).
 *
 * The layer speaks in DTOs, never exposing the JPA entity outside the service boundary.
 */
public interface ProductService {

    /** Creates a new product and returns the persisted representation (with generated id). */
    ProductResponse create(ProductRequest request);

    /** Returns all products. */
    List<ProductResponse> findAll();

    /** Returns one product by id, or throws if it does not exist. */
    ProductResponse findById(Long id);

    /** Case-insensitive search by name fragment. */
    List<ProductResponse> searchByName(String fragment);

    /** Replaces the fields of an existing product; throws if it does not exist. */
    ProductResponse update(Long id, ProductRequest request);

    /** Deletes a product by id; throws if it does not exist. */
    void delete(Long id);
}
