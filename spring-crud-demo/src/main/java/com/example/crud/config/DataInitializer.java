package com.example.crud.config;

import com.example.crud.model.Product;
import com.example.crud.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ============================================================================================
 * DataInitializer — seeds sample data at startup. A hands-on demo of {@code @Bean} / IoC.
 * ============================================================================================
 *
 * <h3>{@code @Configuration}</h3>
 * Marks a class that DEFINES beans via {@code @Bean} methods. Spring processes it during startup.
 *
 * <h3>{@code @Bean}</h3>
 * Whereas {@code @Component}/{@code @Service} let Spring create a bean from a class it scans, a
 * {@code @Bean} method lets YOU construct and configure the object yourself and hand it back to the
 * container. Here we return a {@link CommandLineRunner}.
 *
 * <h3>{@code CommandLineRunner}</h3>
 * A functional interface Spring Boot invokes ONCE, right after the application context is ready.
 * Perfect for one-off startup work like seeding.
 *
 * <h3>DI into a {@code @Bean} method</h3>
 * The {@code ProductRepository} parameter is injected by Spring — method parameters of {@code @Bean}
 * methods are resolved from the container, exactly like constructor parameters.
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner seedProducts(ProductRepository repository) {
        // A lambda implementing CommandLineRunner.run(String... args).
        return args -> {
            if (repository.count() > 0) {
                return; // idempotent: don't double-seed if data already exists
            }
            repository.save(new Product("Keyboard", "Mechanical, tactile switches", 79.99, 25));
            repository.save(new Product("Mouse", "Wireless ergonomic mouse", 39.50, 40));
            repository.save(new Product("Monitor", "27-inch 1440p IPS", 249.00, 12));
            log.info("Seeded {} sample products", repository.count());
        };
    }
}
