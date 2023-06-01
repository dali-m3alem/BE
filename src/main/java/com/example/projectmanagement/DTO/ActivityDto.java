package com.example.projectmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDto {
    private Long id;
    private String activityName;
    private String descriptionA;
    private String objectiveA;
    private String durationA;
    private Date deadlineA;
    private Long projectId;
    private String status;
    private String teamName
            ;
    private String projectName;

}
