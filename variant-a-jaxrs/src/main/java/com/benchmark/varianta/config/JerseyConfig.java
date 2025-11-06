package com.benchmark.varianta.config;

import com.benchmark.varianta.resource.CategoryResource;
import com.benchmark.varianta.resource.ItemResource;
import com.benchmark.varianta.resource.MetricsResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

public class JerseyConfig extends ResourceConfig {
    
    public JerseyConfig() {
        // Register resources
        register(CategoryResource.class);
        register(ItemResource.class);
        register(MetricsResource.class);
        
        // Register Jackson for JSON
        register(JacksonFeature.class);
        register(ObjectMapperProvider.class);
        
        // Register Bean Validation
        register(org.glassfish.jersey.server.validation.ValidationFeature.class);
        
        // Disable WADL generation for performance
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
        
        // Enable validation error responses
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    }
    
    @Provider
    public static class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
        private final ObjectMapper mapper;
        
        public ObjectMapperProvider() {
            mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        }
        
        @Override
        public ObjectMapper getContext(Class<?> type) {
            return mapper;
        }
    }
}
