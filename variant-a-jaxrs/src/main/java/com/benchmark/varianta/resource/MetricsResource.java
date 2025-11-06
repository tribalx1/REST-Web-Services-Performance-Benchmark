package com.benchmark.varianta.resource;

import com.benchmark.varianta.VariantAApplication;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/actuator")
public class MetricsResource {
    
    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response health() {
        return Response.ok("{\"status\":\"UP\"}").build();
    }
    
    @GET
    @Path("/prometheus")
    @Produces("text/plain; version=0.0.4")
    public Response prometheus() {
        PrometheusMeterRegistry registry = (PrometheusMeterRegistry) VariantAApplication.getMeterRegistry();
        String metrics = registry.scrape();
        return Response.ok(metrics).build();
    }
}
