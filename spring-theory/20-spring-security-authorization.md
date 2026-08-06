# 20. Spring Security — Authorization

> Authorization decides what an authenticated or anonymous caller may do. Interviewers expect URL authorization with `authorizeHttpRequests`, roles vs authorities, method security, and API concerns such as CORS, CSRF, and stateless JWT resource servers.

## Core Concepts

### URL Authorization
`authorizeHttpRequests` configures access rules for request matchers. Rules are evaluated in order, so specific matchers should appear before broad ones.

### Role vs Authority
An authority is a permission string such as `orders:read`. A role is a conventionally prefixed authority: `ROLE_ADMIN`. `hasRole("ADMIN")` checks for `ROLE_ADMIN`; `hasAuthority("ROLE_ADMIN")` checks the exact string.

### Method Security
`@EnableMethodSecurity` enables annotations such as `@PreAuthorize`, `@PostAuthorize`, `@Secured`, and `@RolesAllowed`. Method security protects service methods even when called from different entry points.

### CORS
CORS is a browser security mechanism controlling which origins can call your API. It is not authentication; it is cross-origin access control enforced by browsers.

### CSRF
CSRF protects browser session or cookie-authenticated applications from forged state-changing requests. Stateless bearer-token APIs commonly disable CSRF because credentials are not automatically attached by browsers.

### Stateless JWT Resource Server
A resource server validates bearer JWTs, converts claims to authorities, and does not require an HTTP session.

## How It Works

After authentication populates the `SecurityContext`, authorization filters compare the current request against configured rules. For method security, Spring AOP intercepts method calls and evaluates expressions such as `hasAuthority` or custom permission checks before invoking the method.

## Code Examples

```java
package com.example.security.authz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
class AuthorizationSecurityConfig {

    @Bean
    SecurityFilterChain authorizationSecurity(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/orders/**")
                            .hasAuthority("orders:read")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/orders/**")
                            .hasAuthority("orders:write")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthorities())))
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthorities() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("permissions");
        converter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://app.example.com"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}

@Service
class OrderAuthorizationService {

    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('orders:read')")
    String readOrder(long id) {
        return "order-" + id;
    }

    @org.springframework.security.access.prepost.PreAuthorize("#owner == authentication.name or hasRole('ADMIN')")
    void updateOwnOrder(String owner) {
        // SpEL can compare method arguments with the authenticated principal.
    }

    @org.springframework.security.access.annotation.Secured("ROLE_SUPPORT")
    void supportOnly(Authentication authentication) {
        // @Secured uses role-style strings and is less expressive than @PreAuthorize.
    }
}
```

## Common Interview Questions

- **Q:** What is authorization? **A:** Deciding whether the current principal can access a resource or perform an action.
- **Q:** How does `hasRole("ADMIN")` work? **A:** It checks for the authority `ROLE_ADMIN`.
- **Q:** What is the difference between role and authority? **A:** Authorities are exact permissions; roles are a `ROLE_` naming convention.
- **Q:** Why enable method security? **A:** It protects business methods regardless of which controller or message entry point calls them.
- **Q:** What is `@PreAuthorize`? **A:** A method-security annotation that evaluates a SpEL expression before method execution.
- **Q:** What is CORS? **A:** Browser-enforced cross-origin permission policy, not an authentication mechanism.
- **Q:** When should CSRF be enabled? **A:** For browser apps using cookies/sessions for authentication.
- **Q:** Why disable CSRF for stateless JWT APIs? **A:** Bearer tokens are sent explicitly in headers, not automatically attached like cookies.
- **Q:** Why does matcher order matter? **A:** The first matching rule can determine access, so broad rules can shadow specific ones.

## Pitfalls & Best Practices

- Put public and specific matchers before broad authenticated rules.
- Prefer fine-grained authorities for APIs and map roles only when they match the domain.
- Do not rely only on front-end hiding of buttons; enforce authorization server-side.
- Enable method security for sensitive service operations.
- Configure CORS narrowly; avoid wildcard origins with credentials.
- Keep CSRF enabled for session-based browser applications.

## Related Topics

- 19 Spring Security — Authentication
- 12 Building REST APIs
- 13 HTTP Status Codes for APIs
