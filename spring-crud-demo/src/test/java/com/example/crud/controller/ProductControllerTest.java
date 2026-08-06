package com.example.crud.controller;

import com.example.crud.dto.ProductResponse;
import com.example.crud.exception.ResourceNotFoundException;
import com.example.crud.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ============================================================================================
 * ProductControllerTest — tests the WEB layer in isolation (no real service, no DB).
 * ============================================================================================
 *
 * <h3>{@code @WebMvcTest(ProductController.class)}</h3>
 * A slice test that loads ONLY the MVC layer for the named controller (plus the
 * {@code GlobalExceptionHandler} advice) — not services or repositories. Fast and focused on
 * HTTP concerns: routing, status codes, JSON, validation.
 *
 * <h3>{@code @MockBean}</h3>
 * Places a Mockito mock of {@code ProductService} into the test's application context, so the
 * controller's injected dependency is our stub. We script its behavior per test.
 *
 * <h3>{@code MockMvc}</h3>
 * Simulates HTTP requests against the controller WITHOUT starting a real server.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Test
    void getAllReturnsJsonArray() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                new ProductResponse(1L, "Keyboard", "desc", 79.99, 25)));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Keyboard"));
    }

    @Test
    void getMissingReturns404() throws Exception {
        when(service.findById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Product", 99));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createWithBlankNameReturns400() throws Exception {
        // name is blank -> @NotBlank fails -> handled by GlobalExceptionHandler as 400.
        String badJson = "{\"name\":\"\",\"description\":\"x\",\"price\":1.0,\"quantity\":1}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }
}
