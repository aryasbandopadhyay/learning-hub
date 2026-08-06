package com.example.hub;

import com.example.hub.config.ContentProperties;
import com.example.hub.config.JudgeProperties;
import com.example.hub.config.ProgressProperties;
import com.example.hub.config.AuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * ============================================================================================
 * LearningHubApplication — the Spring Boot entry point.
 * ============================================================================================
 * <p>{@code @SpringBootApplication} is a meta-annotation bundling three things:
 * <ul>
 *   <li>{@code @Configuration} — this class can define beans.</li>
 *   <li>{@code @EnableAutoConfiguration} — Spring Boot auto-wires an embedded Tomcat, Jackson,
 *       static-resource serving, etc. based on what's on the classpath.</li>
 *   <li>{@code @ComponentScan} — discovers our {@code @RestController}/{@code @Service} beans in
 *       this package and below.</li>
 * </ul>
 *
 * <p>{@code @EnableConfigurationProperties(ContentProperties.class)} registers our typed,
 * immutable configuration bean bound from {@code application.yml} (the {@code hub.*} keys).
 * This is the crux of the app's extensibility: all categories/paths live in config, so adding
 * a new subject area (e.g. DSA) never touches this code.
 */
@SpringBootApplication
@EnableConfigurationProperties({ContentProperties.class, JudgeProperties.class, ProgressProperties.class, AuthProperties.class})
public class LearningHubApplication {

    public static void main(String[] args) {
        // Boots the whole application context and starts the embedded web server.
        SpringApplication.run(LearningHubApplication.class, args);
    }
}
