package com.example.hub;

import com.example.hub.controller.ContentController;
import com.example.hub.model.CategoryDto;
import com.example.hub.model.FileContent;
import com.example.hub.model.TreeNode;
import com.example.hub.service.ContentService;
import com.example.hub.web.AuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer test for {@link ContentController}. {@code @WebMvcTest} loads ONLY the MVC slice and
 * we replace {@link ContentService} with a Mockito mock ({@code @MockitoBean}) so the test is
 * fast and focused on request/response wiring (routing, params, JSON serialization).
 *
 * <p>The app-wide {@link AuthFilter} (a {@code @Component}) is excluded from this slice — it
 * gates real requests but is orthogonal to the controller wiring under test and would otherwise
 * drag {@code AuthService}/{@code AuthProperties} into the minimal context.
 */
@WebMvcTest(controllers = ContentController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE, classes = AuthFilter.class))
class ContentControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ContentService content;

    @Test
    void categoriesEndpointReturnsJson() throws Exception {
        when(content.categories()).thenReturn(List.of(
                new CategoryDto("springboot", "Spring Boot", "demo"),
                new CategoryDto("lld", "LLD", "designs")));

        mvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("springboot"))
                .andExpect(jsonPath("$[1].label").value("LLD"));
    }

    @Test
    void treeEndpointReturnsTree() throws Exception {
        TreeNode root = TreeNode.dir("LLD", "", List.of(
                TreeNode.file("README.md", "parking-lot/README.md", "md")));
        when(content.tree(eq("lld"))).thenReturn(root);

        mvc.perform(get("/api/tree/lld"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("dir"))
                .andExpect(jsonPath("$.children[0].name").value("README.md"));
    }

    @Test
    void fileEndpointReturnsContent() throws Exception {
        when(content.file(eq("hld"), eq("system-design-hld/url-shortener.md")))
                .thenReturn(new FileContent("hld", "system-design-hld/url-shortener.md",
                        "url-shortener.md", "md", true, "# URL Shortener"));

        mvc.perform(get("/api/file")
                        .param("category", "hld")
                        .param("path", "system-design-hld/url-shortener.md"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.markdown").value(true))
                .andExpect(jsonPath("$.content").value("# URL Shortener"));
    }
}
