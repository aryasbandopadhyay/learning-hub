package com.example.crud.service;

import com.example.crud.dto.ProductRequest;
import com.example.crud.dto.ProductResponse;
import com.example.crud.exception.ResourceNotFoundException;
import com.example.crud.mapper.ProductMapper;
import com.example.crud.model.Product;
import com.example.crud.repository.ProductRepository;
import com.example.crud.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * ============================================================================================
 * ProductServiceImplTest — a PURE unit test of the business logic (no Spring, no DB).
 * ============================================================================================
 *
 * This is the payoff of constructor injection + programming to interfaces: we can test the service
 * in isolation by handing it FAKE collaborators.
 *
 * <h3>{@code @ExtendWith(MockitoExtension.class)}</h3>
 * Enables Mockito's JUnit 5 integration so {@code @Mock}/{@code @InjectMocks} are processed.
 *
 * <ul>
 *   <li>{@code @Mock} creates a stub {@code ProductRepository} whose behavior we script.</li>
 *   <li>{@code @InjectMocks} constructs the real {@code ProductServiceImpl}, injecting the mocks
 *       into its constructor.</li>
 * </ul>
 * No Spring context is started, so these tests run in milliseconds.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    // Use a real mapper — it has no external dependencies and pure conversion logic.
    private final ProductMapper mapper = new ProductMapper();

    @Test
    void createReturnsPersistedProduct() {
        ProductServiceImpl service = new ProductServiceImpl(repository, mapper);
        ProductRequest request = new ProductRequest("Pen", "Blue ink", 1.50, 200);

        Product saved = new Product("Pen", "Blue ink", 1.50, 200);
        // The entity's id is set only by JPA on persist; here we set it reflectively to simulate
        // what the repository would return after an INSERT.
        ReflectionTestUtils.setField(saved, "id", 7L);
        when(repository.save(org.mockito.ArgumentMatchers.any(Product.class))).thenReturn(saved);

        ProductResponse response = service.create(request);

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.name()).isEqualTo("Pen");
    }

    @Test
    void findByIdThrowsWhenMissing() {
        ProductServiceImpl service = new ProductServiceImpl(repository, mapper);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
