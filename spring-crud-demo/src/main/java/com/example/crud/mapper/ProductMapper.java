package com.example.crud.mapper;

import com.example.crud.dto.ProductRequest;
import com.example.crud.dto.ProductResponse;
import com.example.crud.model.Product;
import org.springframework.stereotype.Component;

/**
 * ============================================================================================
 * ProductMapper — converts between the entity ({@link Product}) and the API DTOs.
 * ============================================================================================
 *
 * This isolates all "translation" logic in one place (Single Responsibility Principle).
 *
 * <h3>{@code @Component}</h3>
 * The most generic stereotype annotation. It tells component-scanning to register ONE instance of
 * this class as a bean in the IoC container. That single instance (a "singleton" by default) is
 * then injected into the service via constructor injection — demonstrating DI with a plain helper
 * bean (not a controller/service/repository).
 */
@Component
public class ProductMapper {

    /** Builds a NEW entity from an incoming request (used on create). */
    public Product toEntity(ProductRequest request) {
        return new Product(
                request.name(),
                request.description(),
                request.price(),
                request.quantity());
    }

    /** Copies request fields onto an EXISTING managed entity (used on update). */
    public void updateEntity(Product target, ProductRequest request) {
        target.setName(request.name());
        target.setDescription(request.description());
        target.setPrice(request.price());
        target.setQuantity(request.quantity());
    }

    /** Converts an entity into the response DTO returned to clients. */
    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity());
    }
}
