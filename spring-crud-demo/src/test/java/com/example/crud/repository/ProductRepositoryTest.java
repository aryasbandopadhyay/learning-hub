package com.example.crud.repository;

import com.example.crud.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ============================================================================================
 * ProductRepositoryTest — tests the persistence layer against a real (in-memory) database.
 * ============================================================================================
 *
 * <h3>{@code @DataJpaTest}</h3>
 * A "slice" test annotation: it loads ONLY the JPA-related beans (entities, repositories, an
 * embedded H2 datasource) instead of the whole application — so it's fast and focused. Each test
 * runs in a transaction that is rolled back afterwards, keeping tests isolated.
 *
 * {@code @Autowired} performs FIELD injection of the repository. (Constructor injection is
 * preferred in production code, but field injection is common and convenient in tests.)
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    void savesAndGeneratesId() {
        Product saved = repository.save(new Product("Cable", "USB-C", 9.99, 100));
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findsByNameFragmentCaseInsensitively() {
        repository.save(new Product("Wireless Mouse", "desc", 20.0, 5));
        repository.save(new Product("Wired Keyboard", "desc", 30.0, 5));

        List<Product> results = repository.findByNameContainingIgnoreCase("wireless");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Wireless Mouse");
    }
}
