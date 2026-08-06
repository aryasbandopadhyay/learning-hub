package com.example.crud.dto;

/**
 * ============================================================================================
 * ProductResponse — the OUTPUT DTO returned to clients.
 * ============================================================================================
 *
 * Keeping a separate response type (rather than serializing the {@code Product} entity) means:
 * <ul>
 *   <li>We choose exactly which fields are exposed (here: including the generated {@code id}).</li>
 *   <li>Internal/lazy JPA fields never leak into JSON accidentally.</li>
 * </ul>
 *
 * Being a {@code record}, Jackson serializes its components to JSON:
 * {@code {"id":1,"name":"...","description":"...","price":9.99,"quantity":5}}.
 */
public record ProductResponse(
        Long id,
        String name,
        String description,
        double price,
        int quantity
) {
}
