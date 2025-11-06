package com.benchmark.variantd.config;

import com.benchmark.model.Category;
import com.benchmark.model.Item;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Spring Data REST configuration to expose entity IDs in responses
 * and configure CORS if needed
 */
@Configuration
public class RepositoryRestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config,
            CorsRegistry cors) {
        
        // Expose entity IDs in JSON responses
        config.exposeIdsFor(Category.class, Item.class);
        
        // Disable HAL if you want plain JSON (uncomment if needed for testing)
        // config.setDefaultMediaType(MediaType.APPLICATION_JSON);
        // config.useHalAsDefaultJsonMediaType(false);
    }
}
