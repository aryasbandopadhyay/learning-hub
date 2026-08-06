package com.example.crud.repository;

import com.example.crud.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================================================
 * ProductRepository — the data-access layer, powered by Spring Data JPA.
 * ============================================================================================
 *
 * <h3>The "magic": you write an interface, Spring writes the implementation.</h3>
 * By extending {@link JpaRepository JpaRepository&lt;Product, Long&gt;} (entity type, id type),
 * this interface INHERITS a full set of CRUD methods with NO code from you:
 * <ul>
 *   <li>{@code save(entity)} / {@code saveAll(...)} — insert or update</li>
 *   <li>{@code findById(id)} — returns {@code Optional<Product>}</li>
 *   <li>{@code findAll()} — all rows</li>
 *   <li>{@code existsById(id)}, {@code count()}, {@code deleteById(id)}, {@code delete(entity)}</li>
 * </ul>
 * At startup Spring Data creates a proxy bean implementing this interface and registers it in the
 * IoC container, so it can be injected wherever a {@code ProductRepository} is needed.
 *
 * <h3>Derived query methods</h3>
 * Spring Data can also DERIVE a query from a method NAME. {@code findByNameContainingIgnoreCase}
 * is parsed into "WHERE name LIKE %?% (case-insensitive)" — again, no implementation needed.
 *
 * <h3>Annotations</h3>
 * <ul>
 *   <li>{@code @Repository} — a stereotype marking this as a data-access bean. It is optional here
 *       (Spring Data detects repositories anyway) but it documents intent AND enables translation
 *       of low-level persistence exceptions into Spring's {@code DataAccessException} hierarchy.</li>
 * </ul>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * DERIVED QUERY: Spring generates the SQL from this method's name at startup.
     * "findBy" + "Name" + "Containing" (LIKE %..%) + "IgnoreCase".
     *
     * @param fragment substring to search for within the product name
     * @return matching products (possibly empty, never null)
     */
    List<Product> findByNameContainingIgnoreCase(String fragment);
}
