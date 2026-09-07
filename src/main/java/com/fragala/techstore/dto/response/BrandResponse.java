package com.fragala.techstore.dto.response;

import lombok.Getter;

@Getter 
public class BrandResponse {

    private Long id;
    private String name;


public BrandResponse (Long id, String name){
    this.id = id;
    this.name = name;
}
    
}
