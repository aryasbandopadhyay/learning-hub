package com.example.hub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * ============================================================================================
 * ContentProperties — typed binding of the {@code hub.*} keys in application.yml.
 * ============================================================================================
 * <p>Using a Java {@code record} gives us an immutable, constructor-bound configuration object
 * (Spring Boot 3 supports "constructor binding" for records out of the box). The whole content
 * model — which subjects (categories) exist and which folders each exposes — is data here, not
 * code. That is what makes the app trivially extensible:
 *
 * <pre>
 *   hub:
 *     root: ""                      &lt;- {@link #root()}
 *     categories:                   &lt;- {@link #categories()}
 *       - id: dsa
 *         label: DSA
 *         description: "..."
 *         paths: [ dsa ]
 * </pre>
 *
 * Add a block, restart, and a new tab appears — no Java or JS changes.
 *
 * @param root       base directory that category {@code paths} resolve against; blank means
 *                   "use the parent of the working directory" (resolved in the service).
 * @param categories the ordered list of subject areas to expose.
 */
@ConfigurationProperties(prefix = "hub")
public record ContentProperties(
        String root,
        List<Category> categories
) {

    /**
     * One subject area (a tab in the UI).
     *
     * @param id          URL-safe id used in API routes (e.g. {@code lld}).
     * @param label       human label shown on the tab (e.g. {@code LLD}).
     * @param description optional blurb shown above the tree; may be null/blank.
     * @param paths       paths (relative to {@link ContentProperties#root()}) to expose. Each may
     *                    be a file or a directory (walked recursively). Doubles as the security
     *                    allow-list — nothing outside these paths is ever served.
     */
    public record Category(
            String id,
            String label,
            String description,
            List<String> paths
    ) {
    }
}
