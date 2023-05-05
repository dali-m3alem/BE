package com.example.projectmanagement.resource;

import com.example.projectmanagement.DTO.ActivityDto;
import com.example.projectmanagement.Domaine.Activity;
import com.example.projectmanagement.Service.ActivityImplServ;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityImplServ activityService;

    @GetMapping("/getActivityByProjectId/{id}")
    public List<Activity> getActivityByProjectId(@PathVariable Long id) {
        return activityService.getActivityByProjectId(id);

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
