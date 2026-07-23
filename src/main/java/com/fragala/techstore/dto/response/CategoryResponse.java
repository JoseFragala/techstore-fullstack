package com.fragala.techstore.dto.response;


public class CategoryResponse {
    
    private Long id;
    private String name;

// constructor to manipulate the content.

    public CategoryResponse (Long id, String name) {
        this.id = id;
        this.name = name;
    }


// manualy getters
    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }
    

}
