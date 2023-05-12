package com.example.projectmanagement.resource;

import com.example.projectmanagement.DTO.*;
import com.example.projectmanagement.Domaine.Project;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Service.ProjectImplServ;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ProjectController {
    @Autowired
    private ProjectImplServ projectService;

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> getAllProjectsByAdminId(@RequestParam Long adminId) {
        List<ProjectDto> projects = projectService.getAllProjectsByAdminId(adminId);
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/projectsmanager")
    public ResponseEntity<List<ProjectDto>> getAllProjectsByManagerId(@RequestParam Long managerId) {
        List<ProjectDto> projects = projectService.getAllProjectsByManagerId(managerId);
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> countProjects() {
        Long count = projectService.countProjects();
        return ResponseEntity.ok(count);
    }
    @GetMapping("/total")
    public ResponseEntity<Long> getTotalBudget() {
        Optional<Long> totalBudget = projectService.getTotalBudget();
        if (totalBudget.isPresent()) {
            return ResponseEntity.ok(totalBudget.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @GetMapping("/getProjectById/{id}")
    public Project getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PostMapping("/createProject1")
    public ResponseEntity<Void> addProjectWithActivities(@RequestBody ProjectAndActivitiesDto projectAndActivitiesDto) {
        projectService.addProjectWithActivities(projectAndActivitiesDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/createProject")
    public ResponseEntity<Project> createProject(@RequestBody ProjectRequest projectRequest) {
        Project Project = projectService.createProject(projectRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(Project);
    }



    @PutMapping("/updateProject")
    public ResponseEntity<?> updateProject(@RequestBody ProjectRequest projectRequest) {
        try {  Project project = projectService.updateProject(projectRequest);
            return ResponseEntity.ok(project);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("User not found");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteProject")
    public ResponseEntity<?> deleteProject(@RequestParam Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }

}
