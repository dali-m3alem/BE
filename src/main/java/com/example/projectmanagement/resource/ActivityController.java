package com.example.projectmanagement.resource;

import com.example.projectmanagement.DTO.ActivityDto;
import com.example.projectmanagement.Domaine.Activity;
import com.example.projectmanagement.Service.ActivityImplServ;
import com.example.projectmanagement.config.JwtService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityImplServ activityService;

    @Autowired
    private final JwtService jwtService;


    @GetMapping("/getActivityByProjectId/{id}")
    public List<?> getActivityByProjectId(@PathVariable Long id, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        System.out.println(jwt);
        Long managerId = Long.valueOf(jwtService.extractId(jwt));
        return activityService.getActivityByProjectId(id,managerId);
    }

    @GetMapping("/getActivityById/{id}")
    public Activity getActivityById(@PathVariable Long id) {
        return  activityService.getActivityById(id);

    }
    @PreAuthorize("hasAuthority('manager')")
    @PostMapping("/createActivity")
    public ResponseEntity<?> createActivity(@RequestBody ActivityDto activityDto) {
        try {
           activityService.createActivity(activityDto);
            return ResponseEntity.ok(new JSONObject().put("message", "activity created").toString());


        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @PreAuthorize("hasAuthority('manager')")
    @PutMapping("/activities/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable(value = "id") Long id,
                                                    @RequestBody ActivityDto activityDto) {
        if (activityDto.getId() == null || !activityDto.getId().equals(id)) {
            throw new IllegalArgumentException("Activity id does not match path variable");
        }

        Activity activity = activityService.updateActivity(activityDto);

        return ResponseEntity.ok(activity);
    }
    @PreAuthorize("hasAuthority('manager')")
    @DeleteMapping("/deleteActivity/{idu}")
    public void deleteActivity(@PathVariable("idu") Long idUser)
    {
     activityService.deleteActivity(idUser);
    }
    @GetMapping("/{activityId}/team-members")
    public ResponseEntity<List<String>> getTeamMembersByActivityId(@PathVariable Long activityId) {
        List<String> teamMembers = activityService.getAllTeamMembersByActivityId(activityId);
        return ResponseEntity.ok(teamMembers);
    }
    @GetMapping("/activityCount/{status}")
    public ResponseEntity<Long> countTasksByStatus(@PathVariable String status) {
        Long count = activityService.countActivitiesByStatus(status);
        return ResponseEntity.ok(count);
    }
    @GetMapping("/activityPercent/{state}")
    public ResponseEntity<Integer> calculatePercentByState(@PathVariable String state) {
        Long totalTasks = activityService.countActivitiesByStatus("not started");
        Long stateTasks = activityService.countActivitiesByStatus(state);

        if (totalTasks == 0) {
            return ResponseEntity.badRequest().body(0);
        }

        double percent = (stateTasks.doubleValue() / totalTasks.doubleValue()) * 100.0;
        int roundedPercent = (int) Math.round(percent);

        return ResponseEntity.ok(roundedPercent);
    }


    @GetMapping("/countactiv")
    public ResponseEntity<Long> countProjects() {
        Long count = activityService.countactvi();
        return ResponseEntity.ok(count);
    }
}
