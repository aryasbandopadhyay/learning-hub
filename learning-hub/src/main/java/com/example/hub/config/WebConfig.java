package com.example.hub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ============================================================================================
 * WebConfig — Spring MVC customization for how static assets are cached by browsers.
 * ============================================================================================
 * <p>The single-page app ships as classpath static files ({@code index.html}, {@code app.js},
 * {@code features.js}, {@code styles.css}). By default Spring Boot serves these with no
 * {@code Cache-Control} header — only {@code Last-Modified} — which lets browsers (notably Chrome)
 * <em>heuristically</em> cache them for a while. After a redeploy that means users keep running an
 * old {@code app.js}/{@code features.js} until they manually hard-refresh, so bug fixes and
 * routing changes silently don't appear.
 *
 * <p>This config re-registers the {@code /**} resource handler with
 * {@code Cache-Control: no-cache, must-revalidate}. {@code no-cache} does <strong>not</strong>
 * mean "don't cache" — it means "you may store it, but revalidate with the server before every
 * use". Combined with the ETag/Last-Modified validators the resource chain adds, the browser
 * issues a cheap conditional GET each load: the server answers {@code 304 Not Modified} (no body)
 * when nothing changed, and {@code 200} with the fresh file immediately after a deploy. Net effect:
 * new builds show up in every browser without a manual cache clear, at near-zero extra bandwidth.
 *
 * <p>Annotations:
 * <ul>
 *   <li>{@code @Configuration} — a Spring bean-definition/config class, picked up by component scan.</li>
 *   <li>implementing {@code WebMvcConfigurer} — the hook interface Spring MVC calls to let us tweak
 *       the framework (here, {@code addResourceHandlers}) without disabling Boot's auto-config.</li>
 * </ul>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Serve everything under {@code classpath:/static/} but tell browsers to revalidate before
     * reusing a cached copy. {@code resourceChain(true)} keeps Boot's resource resolution/caching
     * on the server side and ensures Last-Modified/ETag validators are emitted for conditional GETs.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.noCache().mustRevalidate())
                .resourceChain(true);
    }
}
