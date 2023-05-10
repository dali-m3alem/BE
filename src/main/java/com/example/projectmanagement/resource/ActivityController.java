package com.example.projectmanagement.resource;

import com.example.projectmanagement.DTO.ActivityDto;
import com.example.projectmanagement.Domaine.Activity;
import com.example.projectmanagement.Service.ActivityImplServ;
import com.example.projectmanagement.config.JwtService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getActivityByProjectId(@PathVariable Long id, HttpServletRequest request) {
        try{
            final String authHeader = request.getHeader("Authorization");
            String jwt = authHeader.substring(7);
            System.out.println(jwt);
            Long managerId = Long.valueOf(jwtService.extractId(jwt));
            return ResponseEntity.status(HttpStatus.OK).body(activityService.getActivityByProjectId(id, managerId));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " +  e.getMessage());
        }

    }

    @GetMapping("/getActivityById/{id}")
    public Activity getActivityById(@PathVariable Long id) {
        return  activityService.getActivityById(id);

    }

    @PostMapping("/createActivity")
    public ResponseEntity<?> createActivity(@RequestBody ActivityDto activityDto) {
        try {
           activityService.createActivity(activityDto);
            return ResponseEntity.ok(new JSONObject().put("message", "activity created").toString());


        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/activities/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable(value = "id") Long id,
                                                    @RequestBody ActivityDto activityDto) {
        if (activityDto.getId() == null || !activityDto.getId().equals(id)) {
            throw new IllegalArgumentException("Activity id does not match path variable");
        }

        Activity activity = activityService.updateActivity(activityDto);

        return ResponseEntity.ok(activity);
    }

    @DeleteMapping("/deleteActivity/{idu}")
    public void deleteActivity(@PathVariable("idu") Long idUser)
    {
     activityService.deleteActivity(idUser);
    }

}
