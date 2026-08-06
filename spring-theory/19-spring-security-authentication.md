# 19. Spring Security — Authentication

> Authentication proves who the caller is. Spring Security 6 interviews emphasize the security filter chain, lambda DSL configuration, `UserDetailsService`, `PasswordEncoder`, `AuthenticationManager`, and modern form, HTTP Basic, and JWT patterns.

## Core Concepts

### SecurityFilterChain
Spring Security is implemented as servlet filters. In Boot 3/Security 6, configure a `SecurityFilterChain` bean with the lambda DSL instead of the legacy adapter style.

### Authentication Flow
An authentication filter extracts credentials, creates an unauthenticated `Authentication` token, and passes it to `AuthenticationManager`. A provider verifies credentials and returns an authenticated token stored in the `SecurityContext`.

### UserDetailsService
`UserDetailsService` loads user data by username. It is commonly backed by a database and returns `UserDetails` containing username, password hash, enabled flags, and authorities.

### PasswordEncoder and BCrypt
Passwords must be stored as hashes, not plaintext. `BCryptPasswordEncoder` is adaptive and salted. `DelegatingPasswordEncoder` supports `{bcrypt}` prefixes and future migrations.

### Form, Basic, and JWT
Form login is browser/session-oriented. HTTP Basic sends credentials per request and is common for simple machine clients over TLS. JWT bearer tokens are stateless credentials commonly used for APIs and resource servers.

## How It Works

`DelegatingFilterProxy` connects the servlet container to Spring Security's filter chain. Filters handle CORS, CSRF, sessions, authentication mechanisms, exception translation, and authorization. On successful authentication, the `SecurityContext` is populated for the current request and possibly saved in an HTTP session unless stateless behavior is configured.

## Code Examples

```java
package com.example.security.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class AuthenticationSecurityConfig {

    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(basic -> { })
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> { }))
                .build();
    }

    @Bean
    UserDetailsService users(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("alice")
                .password(passwordEncoder.encode("interview-demo-password"))
                .authorities("orders:read", "orders:write")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        // Useful when implementing a custom login endpoint.
        return configuration.getAuthenticationManager();
    }
}
```

## Common Interview Questions

- **Q:** What is the modern Security 6 configuration style? **A:** Define one or more `SecurityFilterChain` beans using the lambda DSL.
- **Q:** What is authentication? **A:** Verifying the caller's identity.
- **Q:** What is `AuthenticationManager`? **A:** The entry point that delegates authentication to providers.
- **Q:** What does `UserDetailsService` do? **A:** Loads user details by username for authentication providers.
- **Q:** Why use `PasswordEncoder`? **A:** To hash and verify passwords safely instead of storing plaintext.
- **Q:** Why BCrypt? **A:** It is salted and intentionally slow, making brute-force attacks harder.
- **Q:** Form login vs Basic? **A:** Form login is session/browser-oriented; Basic sends credentials per request and must use TLS.
- **Q:** What is JWT authentication? **A:** A signed token proves identity/claims without server-side session lookup.
- **Q:** Where is the authenticated user stored? **A:** In the `SecurityContext`, typically accessed through `SecurityContextHolder`.

## Pitfalls & Best Practices

- Never store plaintext passwords.
- Always use TLS for credential-bearing requests.
- Disable CSRF only when the API is truly stateless and not cookie-authenticated.
- Prefer short-lived access tokens and rotate refresh tokens when using JWT.
- Keep authentication and authorization concepts separate.
- Do not put passwords or signing keys in source code.

## Related Topics

- 20 Spring Security — Authorization
- 12 Building REST APIs
- 15 Exception Handling
