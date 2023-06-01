package com.example.projectmanagement.resource;

import com.example.projectmanagement.DTO.*;
import com.example.projectmanagement.Domaine.Project;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Service.ProjectImplServ;
import com.example.projectmanagement.config.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ProjectController {
    @Autowired
    private ProjectImplServ projectService;
    @Autowired
    private final JwtService jwtService;
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> getAllProjectsByAdminId(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        System.out.println(jwt);
        Long adminId = Long.valueOf(jwtService.extractId(jwt));
        List<ProjectDto> projects = projectService.getAllProjectsByAdminId(adminId);
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/projectsmanager")
    public ResponseEntity<List<ProjectDto>> getAllProjectsByManagerId(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        System.out.println(jwt);
        Long managerId = Long.valueOf(jwtService.extractId(jwt));
        List<ProjectDto> projects = projectService.getAllProjectsByManagerId(managerId);
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/countProject")
    public ResponseEntity<Long> countProjects() {
        Long count = projectService.countProjects();
        return ResponseEntity.ok(count);
    }
    @GetMapping("/getProjectById/{id}")
    public Project getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/createProject1")
    public ResponseEntity<Void> addProjectWithActivities(@RequestBody ProjectAndActivitiesDto projectAndActivitiesDto) {
        projectService.addProjectWithActivities(projectAndActivitiesDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/createProject")
    public Project createProject(@RequestBody ProjectRequest projectRequest) {
        return projectService.createProject(projectRequest);
    }

    @PreAuthorize("hasAuthority('admin')")
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
    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/deleteProject")
    public ResponseEntity<?> deleteProject(@RequestParam Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/ChangeProjectStatus/{id}")
    public ResponseEntity<String> changeProjectStatus(@PathVariable Long id) {
        String status = projectService.changeProjectStatus(id);
        if (status != null) {
            return ResponseEntity.ok(status);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/projectCount/{status}")
    public ResponseEntity<Long> countTasksByStatus(@PathVariable String status) {
        Long count = projectService.countProjectsByStatus(status);
        return ResponseEntity.ok(count);
    }
    @GetMapping("/projectPercent/{state}")
    public ResponseEntity<Integer> calculatePercentByState(@PathVariable String state) {
        Long totalProjects = projectService.countProjectsByStatus("not started");
        Long stateProjects = projectService.countProjectsByStatus(state);

        if (totalProjects == 0) {
            return ResponseEntity.badRequest().body(0);
        }

        double percent = (stateProjects.doubleValue() / totalProjects.doubleValue()) * 100.0;
        int roundedPercent = (int) Math.round(percent);

        return ResponseEntity.ok(roundedPercent);
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

}
