package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.ActivityDto;
import com.example.projectmanagement.DTO.ProjectAndActivitiesDto;
import com.example.projectmanagement.DTO.ProjectDto;
import com.example.projectmanagement.DTO.ProjectRequest;
import com.example.projectmanagement.Domaine.Project;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ProjectServ {
    public List<Project> getAllProjects();
    public Project getProjectById(Long id);
    public void addProjectWithActivities(ProjectAndActivitiesDto projectAndActivitiesDto);
    public Project updateProject(ProjectRequest projectRequest);
    public void deleteProject(Long id);
    public Project createProject(ProjectRequest projectRequest) throws AccessDeniedException;
    public List<ProjectDto> getAllProjectsByManagerId(Long managerId);
    public String changeProjectStatus(Long id);
    public Long countProjectsByStatus(String status);
}
