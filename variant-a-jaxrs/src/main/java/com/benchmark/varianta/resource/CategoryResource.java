package com.benchmark.varianta.resource;

import com.benchmark.model.Category;
import com.benchmark.varianta.VariantAApplication;
import com.benchmark.varianta.dto.CategoryDTO;
import com.benchmark.varianta.dto.ItemDTO;
import com.benchmark.varianta.dto.PageResponse;
import com.benchmark.model.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryResource.class);
    private static final String USE_JOIN_FETCH = System.getenv().getOrDefault("USE_JOIN_FETCH", "true");
    
    @GET
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        EntityManager em = VariantAApplication.getEntityManagerFactory().createEntityManager();
        try {
            // Get total count
            Long total = em.createQuery("SELECT COUNT(c) FROM Category c", Long.class)
                    .getSingleResult();
            
            // Get paginated results
            TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c", Category.class);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            
            List<CategoryDTO> categories = query.getResultList().stream()
                    .map(CategoryDTO::fromEntity)
                    .collect(Collectors.toList());
            
            PageResponse<CategoryDTO> response = PageResponse.of(categories, page, size, total);
            return Response.ok(response).build();
            
        } finally {
            em.close();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        EntityManager em = VariantAApplication.getEntityManagerFactory().createEntityManager();
        try {
            Category category = em.find(Category.class, id);
            if (category == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Category not found\"}")
                        .build();
            }
            
            return Response.ok(CategoryDTO.fromEntity(category)).build();
            
        } finally {
            em.close();
        }
    }
    
    @GET
    @Path("/{id}/items")
    public Response getItemsByCategory(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        EntityManager em = VariantAApplication.getEntityManagerFactory().createEntityManager();
        try {
            // Check if category exists
            Category category = em.find(Category.class, id);
            if (category == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Category not found\"}")
                        .build();
            }
            
            // Get total count
            Long total = em.createQuery(
                    "SELECT COUNT(i) FROM Item i WHERE i.category.id = :categoryId", Long.class)
                    .setParameter("categoryId", id)
                    .getSingleResult();
            
            // Get paginated results with optional JOIN FETCH
            String jpql = "true".equalsIgnoreCase(USE_JOIN_FETCH)
                    ? "SELECT i FROM Item i JOIN FETCH i.category WHERE i.category.id = :categoryId"
                    : "SELECT i FROM Item i WHERE i.category.id = :categoryId";
            
            TypedQuery<Item> query = em.createQuery(jpql, Item.class);
            query.setParameter("categoryId", id);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            
            List<ItemDTO> items = query.getResultList().stream()
                    .map(ItemDTO::fromEntity)
                    .collect(Collectors.toList());
            
            PageResponse<ItemDTO> response = PageResponse.of(items, page, size, total);
            return Response.ok(response).build();
            
        } finally {
            em.close();
        }
    }
    
    @POST
    public Response create(@Valid CategoryDTO dto) {
        EntityManager em = VariantAApplication.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            Category category = dto.toEntity();
            em.persist(category);
            
            tx.commit();
            
            return Response.status(Response.Status.CREATED)
                    .entity(CategoryDTO.fromEntity(category))
                    .build();
                    
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error creating category", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } finally {
            em.close();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid CategoryDTO dto) {
        EntityManager em = VariantAApplication.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            Category category = em.find(Category.class, id);
            if (category == null) {
                tx.rollback();
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Category not found\"}")
                        .build();
            }
            
            category.setCode(dto.getCode());
            category.setName(dto.getName());
            em.merge(category);
            
            tx.commit();
            
            return Response.ok(CategoryDTO.fromEntity(category)).build();
                    
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error updating category", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } finally {
            em.close();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        EntityManager em = VariantAApplication.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            Category category = em.find(Category.class, id);
            if (category == null) {
                tx.rollback();
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Category not found\"}")
                        .build();
            }
            
            em.remove(category);
            tx.commit();
            
            return Response.noContent().build();
                    
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error deleting category", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } finally {
            em.close();
        }
    }
}
