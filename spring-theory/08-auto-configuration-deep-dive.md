# 08. Auto-Configuration Deep Dive

> Auto-configuration is Spring Boot's mechanism for creating sensible default beans when the application appears to need them. It matters in interviews because it separates developers who merely use Boot from those who can explain and debug Boot.

## Core Concepts

### `@EnableAutoConfiguration`
Imported by `@SpringBootApplication`, this tells Boot to load candidate auto-configuration classes and evaluate their conditions.

### Conditional Annotations
Common conditions include:
- `@ConditionalOnClass`: class exists on the classpath.
- `@ConditionalOnMissingBean`: user has not defined a replacement bean.
- `@ConditionalOnBean`: another bean exists.
- `@ConditionalOnProperty`: property has a required value.
- `@ConditionalOnWebApplication`: web environment is present.

### Auto-Configuration Metadata
Modern Boot auto-configurations are listed in:
`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

Older Boot versions used `spring.factories` for this purpose.

### Backing Off
Good auto-configuration backs off when the user defines their own bean. This is why adding a custom `ObjectMapper`, `DataSource`, or `SecurityFilterChain` often changes Boot behavior.

## How It Works

During startup, Boot imports auto-configuration classes, sorts them, evaluates conditions against the classpath, bean factory, environment, and web application type, then registers matching bean definitions. Conditions are evaluated in phases so classpath and property checks can happen before bean creation. The condition evaluation report explains why each auto-configuration matched or did not match.

## Code Examples

```java
package com.example.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(name = "com.example.autoconfig.AcmeClient")
class AcmeClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "acme.client", name = "enabled", havingValue = "true", matchIfMissing = true)
    AcmeClient acmeClient(AcmeProperties properties) {
        // Created only if the class exists, property allows it, and no user bean exists.
        return new AcmeClient(properties.baseUrl());
    }

    @Bean
    @ConditionalOnMissingBean
    AcmeProperties acmeProperties() {
        return new AcmeProperties("https://api.example.com");
    }
}

record AcmeProperties(String baseUrl) {
}

class AcmeClient {
    AcmeClient(String baseUrl) {
    }
}
```

## Common Interview Questions

- **Q:** What triggers auto-configuration? **A:** `@EnableAutoConfiguration`, usually through `@SpringBootApplication`.
- **Q:** Does auto-configuration always create beans? **A:** No. It is conditional.
- **Q:** What does `@ConditionalOnMissingBean` do? **A:** It creates a bean only if the user has not already provided one.
- **Q:** Where are auto-configurations listed in Boot 3? **A:** `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
- **Q:** How do you exclude an auto-configuration? **A:** Use `exclude` on `@SpringBootApplication` or `spring.autoconfigure.exclude`.
- **Q:** How do you debug auto-configuration? **A:** Enable debug logging or inspect the condition evaluation report.
- **Q:** Why does adding a custom bean change Boot behavior? **A:** Many auto-configurations back off when user-defined beans are present.
- **Q:** Is auto-configuration component scanning? **A:** No. It imports known configuration classes and evaluates conditions.

## Pitfalls & Best Practices

- Do not fight Boot defaults blindly; inspect the condition report first.
- Prefer overriding with beans or properties before excluding whole auto-configurations.
- Use narrow conditions in custom starters.
- Avoid auto-configurations that require application-specific packages to be scanned.
- Remember that classpath changes can activate unexpected auto-configurations.

## Related Topics

- Spring Boot overview
- Bean registration
- Externalized configuration
- Conditional annotations
- Custom starters

