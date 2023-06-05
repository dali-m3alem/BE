package com.example.projectmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTOs  {
    private Long id;
    private String projectName;
    private String descriptionP;
    private String objectiveP ;

    private Date deadlineP ;
    private String projectManagerEmail;

    // Getters and setters
}

