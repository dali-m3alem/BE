package com.example.projectmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
    private Long TeamId;
    private List<Long> id;
    private String TeamName;
    private String TeamDesc;
    private List<String> emails; // change to a list of emails
    private List<String> activities;

}
