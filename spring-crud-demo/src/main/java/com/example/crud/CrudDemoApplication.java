package com.example.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================================================
 * The application entry point AND the root of Spring's configuration.
 * ============================================================================================
 *
 * <h2>What is the "IoC container"?</h2>
 * Normally YOUR code creates its collaborators with {@code new}. With Spring, you invert that:
 * the framework's <b>IoC (Inversion of Control) container</b> creates and wires objects for you.
 * Those container-managed objects are called <b>beans</b>. "Dependency Injection" (DI) is the
 * mechanism the container uses to hand a bean its collaborators (its "dependencies").
 *
 * <h2>{@code @SpringBootApplication}</h2>
 * This single annotation is a convenience "meta-annotation" that combines three:
 * <ul>
 *   <li>{@code @Configuration} — marks this class as a source of bean definitions.</li>
 *   <li>{@code @EnableAutoConfiguration} — Spring Boot inspects the classpath and auto-configures
 *       sensible beans (e.g. sees H2 + JPA and configures a DataSource, EntityManager, etc.).</li>
 *   <li>{@code @ComponentScan} — scans THIS package and its sub-packages for stereotype-annotated
 *       classes ({@code @Component}, {@code @Service}, {@code @Repository}, {@code @RestController})
 *       and registers them as beans. This is why we never {@code new} our services/controllers.</li>
 * </ul>
 */
@SpringBootApplication
public class CrudDemoApplication {

    /**
     * Standard Java entry point. {@link SpringApplication#run} bootstraps the whole framework:
     * it creates the IoC container (the {@code ApplicationContext}), performs component scanning
     * and auto-configuration, wires every bean via DI, runs any startup runners, and finally
     * starts the embedded Tomcat web server.
     *
     * @param args command-line arguments forwarded to Spring (e.g. --server.port=9090)
     */
    public static void main(String[] args) {
        SpringApplication.run(CrudDemoApplication.class, args);
    }
}
