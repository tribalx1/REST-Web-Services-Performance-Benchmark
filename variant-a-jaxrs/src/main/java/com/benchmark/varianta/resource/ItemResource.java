package com.benchmark.varianta.resource;

import com.benchmark.model.Category;
import com.benchmark.model.Item;
import com.benchmark.varianta.VariantAApplication;
import com.benchmark.varianta.dto.ItemDTO;
import com.benchmark.varianta.dto.PageResponse;
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

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {
    
    private static final Logger logger = LoggerFactory.getLogger(ItemResource.class);
    private static final String USE_JOIN_FETCH = System.getenv().getOrDefault("USE_JOIN_FETCH", "true");
    
    @GET
    public Response getAll(
            @QueryParam("categoryId") Long categoryId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        EntityManager em = VariantAApplication.getEntityManagerFactory().createEntityManager();
        try {
            if (categoryId != null) {
                return getByCategory(categoryId, page, size, em);
            }
            
            // Get total count
            Long total = em.createQuery("SELECT COUNT(i) FROM Item i", Long.class)
                    .getSingleResult();
            
            // Get paginated results with optional JOIN FETCH
            String jpql = "true".equalsIgnoreCase(USE_JOIN_FETCH)
                    ? "SELECT i FROM Item i JOIN FETCH i.category"
                    : "SELECT i FROM Item i";
            
            TypedQuery<Item> query = em.createQuery(jpql, Item.class);
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
    
    private Response getByCategory(Long categoryId, int page, int size, EntityManager em) {
        // Get total count
        Long total = em.createQuery(
                "SELECT COUNT(i) FROM Item i WHERE i.category.id = :categoryId", Long.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();
        
        // Get paginated results with optional JOIN FETCH
        String jpql = "true".equalsIgnoreCase(USE_JOIN_FETCH)
                ? "SELECT i FROM Item i JOIN FETCH i.category WHERE i.category.id = :categoryId"
                : "SELECT i FROM Item i WHERE i.category.id = :categoryId";
        
        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        query.setParameter("categoryId", categoryId);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        
        List<ItemDTO> items = query.getResultList().stream()
                .map(ItemDTO::fromEntity)
                .collect(Collectors.toList());
        
        PageResponse<ItemDTO> response = PageResponse.of(items, page, size, total);
        return Response.ok(response).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        EntityManager em = VariantAApplication.getEntityManagerFactory().createEntityManager();
        try {
            Item item = em.find(Item.class, id);
            if (item == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Item not found\"}")
                        .build();
            }
            
            // Force category loading
            item.getCategory().getId();
            
            return Response.ok(ItemDTO.fromEntity(item)).build();
            
        } finally {
            em.close();
        }
    }
    
    @POST
    public Response create(@Valid ItemDTO dto) {
        EntityManager em = VariantAApplication.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            Category category = em.find(Category.class, dto.getCategoryId());
            if (category == null) {
                tx.rollback();
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Category not found\"}")
                        .build();
            }
            
            Item item = dto.toEntity(category);
            em.persist(item);
            
            tx.commit();
            
            return Response.status(Response.Status.CREATED)
                    .entity(ItemDTO.fromEntity(item))
                    .build();
                    
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error creating item", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } finally {
            em.close();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid ItemDTO dto) {
        EntityManager em = VariantAApplication.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            Item item = em.find(Item.class, id);
            if (item == null) {
                tx.rollback();
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Item not found\"}")
                        .build();
            }
            
            Category category = em.find(Category.class, dto.getCategoryId());
            if (category == null) {
                tx.rollback();
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Category not found\"}")
                        .build();
            }
            
            item.setSku(dto.getSku());
            item.setName(dto.getName());
            item.setPrice(dto.getPrice());
            item.setStock(dto.getStock());
            item.setCategory(category);
            
            em.merge(item);
            tx.commit();
            
            return Response.ok(ItemDTO.fromEntity(item)).build();
                    
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error updating item", e);
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
            
            Item item = em.find(Item.class, id);
            if (item == null) {
                tx.rollback();
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Item not found\"}")
                        .build();
            }
            
            em.remove(item);
            tx.commit();
            
            return Response.noContent().build();
                    
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error deleting item", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } finally {
            em.close();
        }
    }
}
