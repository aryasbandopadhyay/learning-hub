package com.example.crud.service.impl;

import com.example.crud.dto.ProductRequest;
import com.example.crud.dto.ProductResponse;
import com.example.crud.exception.ResourceNotFoundException;
import com.example.crud.mapper.ProductMapper;
import com.example.crud.model.Product;
import com.example.crud.repository.ProductRepository;
import com.example.crud.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ============================================================================================
 * ProductServiceImpl — the concrete business logic. THE key file for understanding DI.
 * ============================================================================================
 *
 * <h3>{@code @Service}</h3>
 * A stereotype (specialization of {@code @Component}) marking a business-logic bean. Component
 * scanning registers a singleton instance in the IoC container.
 *
 * <h3>Dependency Injection via the CONSTRUCTOR</h3>
 * This class needs two collaborators: a {@link ProductRepository} and a {@link ProductMapper}.
 * It does NOT create them with {@code new}. Instead it declares them as {@code final} constructor
 * parameters, and Spring supplies the matching beans when it builds this one. This is
 * <b>constructor injection</b> — the recommended DI style because:
 * <ul>
 *   <li>Dependencies are explicit and {@code final} (immutable, never null).</li>
 *   <li>The class is easy to unit-test: just call {@code new ProductServiceImpl(mockRepo, mapper)}.</li>
 * </ul>
 * NOTE: since Spring 4.3, if a bean has exactly ONE constructor, {@code @Autowired} is optional —
 * Spring uses it automatically. (Field injection with {@code @Autowired} exists but is discouraged.)
 *
 * <h3>{@code @Transactional}</h3>
 * Wraps a method in a database transaction: it commits if the method returns normally and rolls
 * back on a runtime exception. Read-only methods are hinted with {@code readOnly = true} for a
 * minor optimization. Spring implements this with a proxy around the bean (AOP).
 */
@Service
public class ProductServiceImpl implements ProductService {

    // 'final' + assigned only in the constructor => guaranteed injected and immutable.
    private final ProductRepository repository;
    private final ProductMapper mapper;

    /**
     * Spring calls this constructor at startup, passing in the {@code ProductRepository} proxy and
     * the {@code ProductMapper} bean it created during component scanning. That is DI.
     */
    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product entity = mapper.toEntity(request);
        Product saved = repository.save(entity); // INSERT; 'saved' now has the generated id
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse) // method reference: p -> mapper.toResponse(p)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return mapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchByName(String fragment) {
        return repository.findByNameContainingIgnoreCase(fragment == null ? "" : fragment)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        // Load the managed entity (or fail), then mutate it. Because we're inside a transaction,
        // Hibernate's "dirty checking" will flush the UPDATE automatically — save() is optional.
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        mapper.updateEntity(product, request);
        Product saved = repository.save(product);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
        repository.deleteById(id);
    }
}
