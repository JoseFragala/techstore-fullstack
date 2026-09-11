package com.fragala.techstore.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
public class RoleResponse {

    private Long id;

    private String name;

    private String description;

    private LocalDateTime createdAt;
    
}
