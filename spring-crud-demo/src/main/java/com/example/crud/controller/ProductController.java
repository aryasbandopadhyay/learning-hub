package com.example.crud.controller;

import com.example.crud.dto.ProductRequest;
import com.example.crud.dto.ProductResponse;
import com.example.crud.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * ============================================================================================
 * ProductController — the REST/web layer. Maps HTTP requests to service calls.
 * ============================================================================================
 *
 * <h3>{@code @RestController}</h3>
 * Combines {@code @Controller} (a stereotype bean) + {@code @ResponseBody} (return values are
 * serialized directly to the HTTP response body as JSON, not resolved to a view template).
 *
 * <h3>{@code @RequestMapping("/api/products")}</h3>
 * Sets the base URL path for every handler method in this class.
 *
 * <h3>Constructor injection again</h3>
 * The controller depends only on the {@code ProductService} ABSTRACTION. Spring injects the
 * concrete {@code ProductServiceImpl} bean. The controller knows nothing about JPA or H2.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    /**
     * GET /api/products            -> list all
     * GET /api/products?name=abc   -> filter by name fragment
     *
     * {@code @RequestParam(required = false)} binds the optional {@code ?name=} query string.
     */
    @GetMapping
    public List<ProductResponse> list(@RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) {
            return service.searchByName(name);
        }
        return service.findAll();
    }

    /**
     * GET /api/products/{id} -> fetch one.
     * {@code @PathVariable} binds the {id} URL segment to the method parameter.
     */
    @GetMapping("/{id}")
    public ProductResponse getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    /**
     * POST /api/products -> create.
     * <ul>
     *   <li>{@code @RequestBody} deserializes the JSON body into a {@code ProductRequest}.</li>
     *   <li>{@code @Valid} triggers Bean Validation on that DTO (failures -> 400 via the advice).</li>
     * </ul>
     * Returns 201 Created with a {@code Location} header pointing at the new resource — the
     * REST-correct response for a successful creation.
     */
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = service.create(request);
        URI location = URI.create("/api/products/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    /** PUT /api/products/{id} -> full update of an existing product. */
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id,
                                  @Valid @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    /**
     * DELETE /api/products/{id} -> remove.
     * Returns 204 No Content (success, empty body) via {@code @ResponseStatus}-style builder.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
