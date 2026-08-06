package com.example.crud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * ============================================================================================
 * ProductRequest — the INPUT DTO (Data Transfer Object) for create/update requests.
 * ============================================================================================
 *
 * WHY a DTO instead of accepting the {@code Product} entity directly?
 * <ul>
 *   <li>Decoupling: the public API shape is independent of the DB schema.</li>
 *   <li>Security: clients can't set server-owned fields like {@code id}.</li>
 *   <li>Validation: we validate incoming data here, before it ever touches persistence.</li>
 * </ul>
 *
 * This is a Java {@code record} — an immutable data carrier. The compiler generates the
 * constructor, accessors ({@code name()}, {@code price()}...), {@code equals}/{@code hashCode}/
 * {@code toString}. Jackson binds the incoming JSON body to these components.
 *
 * <h3>Bean Validation annotations</h3> (checked when the controller parameter is marked {@code @Valid})
 * <ul>
 *   <li>{@code @NotBlank} — must be non-null and contain non-whitespace text.</li>
 *   <li>{@code @Size} — length bounds.</li>
 *   <li>{@code @Positive} / {@code @PositiveOrZero} — numeric constraints.</li>
 * </ul>
 */
public record ProductRequest(

        @NotBlank(message = "name is required")
        @Size(max = 120, message = "name must be at most 120 characters")
        String name,

        @Size(max = 500, message = "description must be at most 500 characters")
        String description,

        @Positive(message = "price must be greater than 0")
        double price,

        @PositiveOrZero(message = "quantity must be zero or greater")
        int quantity
) {
}
