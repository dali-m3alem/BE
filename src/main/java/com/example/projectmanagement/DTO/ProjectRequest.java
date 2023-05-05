package com.example.projectmanagement.DTO;

import com.example.projectmanagement.Domaine.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {
    private Long id;
    private String projectName;
    private String descriptionP;
    private String objectiveP ;
    private String durationP;
    private Date deadlineP ;
    private Long userId;
    private Long budget;

    private String email;

}
