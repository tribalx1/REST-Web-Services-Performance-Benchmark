package com.benchmark.varianta;

import com.benchmark.varianta.config.JerseyConfig;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class VariantAApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(VariantAApplication.class);
    private static final String BASE_URI = "http://0.0.0.0:8081/";
    
    private static EntityManagerFactory emf;
    private static MeterRegistry meterRegistry;
    
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
    
    public static MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }
    
    public static void main(String[] args) {
        try {
            // Initialize Prometheus metrics
            CollectorRegistry collectorRegistry = new CollectorRegistry();
            meterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT, collectorRegistry, Clock.SYSTEM);
            logger.info("Prometheus metrics initialized");
            
            // Initialize JPA EntityManagerFactory
            emf = Persistence.createEntityManagerFactory("benchmark-pu");
            logger.info("EntityManagerFactory initialized");
            
            // Configure Jersey
            ResourceConfig config = new JerseyConfig();
            
            // Start HTTP server
            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
            
            logger.info("========================================");
            logger.info("Variant A - JAX-RS (Jersey) Started");
            logger.info("========================================");
            logger.info("Application running at: {}", BASE_URI);
            logger.info("Metrics endpoint: {}actuator/prometheus", BASE_URI);
            logger.info("Press Ctrl+C to stop");
            logger.info("========================================");
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down server...");
                server.shutdownNow();
                if (emf != null && emf.isOpen()) {
                    emf.close();
                }
                logger.info("Server stopped");
            }));
            
            // Keep server running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            logger.error("Failed to start server", e);
            System.exit(1);
        }
    }
}
