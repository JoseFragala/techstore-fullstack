package com.fragala.techstore.dto.response;

/**
 * Response DTO returned after category-related operations.
 *
 * <p>This class represents the data the application chooses to expose back to clients. It exists
 * because response DTOs protect the domain model from being coupled directly to the API layer and
 * allow the application to return only the fields that are relevant to the client.
 *
 * <p>Architecturally, response DTOs sit between the service/controller layer and the outside
 * world. Unlike request DTOs, which represent incoming data, response DTOs represent outgoing
 * data shaped for consumers.
 *
 * <p>This DTO is used when the application needs to return category information without exposing
 * the full {@code Category} entity.
 */
public class CategoryResponse {
    
    private Long id;
    private String name;

    /**
     * Creates a response object with the data that should be returned to the client.
     *
     * @param id the identifier of the created or retrieved category
     * @param name the category name to expose in the response
     */
    public CategoryResponse (Long id, String name) {
        this.id = id;
        this.name = name;
    }


    /**
     * Returns the category identifier.
     *
     * @return the category id
     */
    public Long getId(){
        return id;
    }

    /**
     * Returns the category name.
     *
     * @return the category name
     */
    public String getName(){
        return name;
    }
    

}
