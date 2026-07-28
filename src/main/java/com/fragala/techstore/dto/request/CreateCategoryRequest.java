package com.fragala.techstore.dto.request;

/**
 * Request DTO used to receive the data required to create a category.
 *
 * <p>This class represents the input contract for a category creation use case. It exists so the
 * application can receive only the data needed by the use case instead of exposing the internal
 * JPA entity directly to the outside world.
 *
 * <p>Architecturally, request DTOs belong to the API boundary. They help separate external input
 * models from persistence models, which makes validation, versioning, and security easier to
 * manage over time.
 *
 * <p>This DTO is used when a client sends data to create a new category.
 */
public class CreateCategoryRequest {

    // Request DTOs usually contain only the fields the client is allowed to send.
    // That keeps the API contract focused and avoids accidental exposure of entity details.
    private String name;

    /**
     * Returns the category name sent by the client.
     *
     * @return the category name requested for creation
     */
    public String getName(){
        return name;
    }


    
}
