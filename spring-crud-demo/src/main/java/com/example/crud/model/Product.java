package com.example.crud.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ============================================================================================
 * Product — a JPA ENTITY. One instance = one row in the PRODUCTS table.
 * ============================================================================================
 *
 * JPA (Jakarta Persistence API) is the standard specification for Object-Relational Mapping (ORM).
 * Hibernate is the implementation Spring Boot uses. These annotations tell Hibernate how to map
 * this Java object to a database table and back.
 *
 * <h3>Annotations used</h3>
 * <ul>
 *   <li>{@code @Entity} — marks the class as persistent; Hibernate manages it. Requires a no-arg
 *       constructor (provided below) and an @Id.</li>
 *   <li>{@code @Table(name = "products")} — customizes the table name (defaults to the class name).</li>
 *   <li>{@code @Id} — marks the primary key field.</li>
 *   <li>{@code @GeneratedValue(strategy = IDENTITY)} — the DB auto-generates the id (auto-increment).</li>
 *   <li>{@code @Column(...)} — customizes column mapping (nullable, length, etc.).</li>
 * </ul>
 *
 * NOTE: this is a persistence object, NOT the shape we expose over HTTP. We deliberately keep it
 * separate from the API DTOs (see the dto package) so the database schema and the public API can
 * evolve independently — a key design principle.
 */
@Entity
@Table(name = "products")
public class Product {

    /** Primary key. The database assigns it (IDENTITY = auto-increment column). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Product name. {@code nullable = false} maps to a NOT NULL column constraint. */
    @Column(nullable = false, length = 120)
    private String name;

    /** Free-text description; may be null. */
    @Column(length = 500)
    private String description;

    /** Unit price. */
    @Column(nullable = false)
    private double price;

    /** Units in stock. */
    @Column(nullable = false)
    private int quantity;

    /**
     * JPA REQUIRES a no-argument constructor so Hibernate can instantiate the entity via reflection
     * when reading rows from the database. It can be protected/package-private but must exist.
     */
    protected Product() {
    }

    /** Convenience constructor for creating new products in code (e.g. seeding, tests). */
    public Product(String name, String description, double price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    // ---- Getters & setters --------------------------------------------------------------------
    // JPA and Jackson (JSON) use these accessors. The id has no public setter because the
    // database owns it.

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
