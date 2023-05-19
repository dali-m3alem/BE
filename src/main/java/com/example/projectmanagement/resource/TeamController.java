package com.example.projectmanagement.resource;

import com.example.projectmanagement.DTO.TeamDTO;
import com.example.projectmanagement.Domaine.Team;
import com.example.projectmanagement.Service.TeamImplServ;
import com.example.projectmanagement.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class TeamController {


    @Autowired
    private TeamImplServ teamService;
    @Autowired
    private JwtService jwtService;
    @GetMapping("/getTeamByActivityAndProjectAndManager/{activityId}/{projectId}")
    public ResponseEntity<?> getTeamByActivityAndProjectAndManager(
            @PathVariable Long activityId,
            @PathVariable Long projectId,
            HttpServletRequest request
    ) {
        try {
            final String authHeader = request.getHeader("Authorization");
            String jwt = authHeader.substring(7);
            System.out.println(jwt);
            Long managerId = Long.valueOf(jwtService.extractId(jwt));
            Team team = teamService.getTeamByActivityAndProjectAndManager(activityId, projectId, managerId);
            return ResponseEntity.status(HttpStatus.OK).body(team);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/ids")
    public List<String> getAllTeamIds() {
        return teamService.getAllTeamIds();
    }
    @GetMapping(value = "/getAllTeam")
    public List<Team> getAllTeam() {
        return teamService.getAllTeam();
    }

    @PostMapping(value = "/addTeam")
    public ResponseEntity<Team> addTeam(@RequestBody TeamDTO teamRequest) {
        try {
            Team team = teamService.addTeam(teamRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(team);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(null);
        }
    }


    @PutMapping(value = "/updateTeam/{idTeam}")
    public ResponseEntity<Team> updateTeam(@RequestBody  TeamDTO teamRequest) {
        try {
             Team team= teamService.updateTeam(teamRequest);
            return ResponseEntity.status(HttpStatus.OK).body(team);

        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(null);
        }
    }

    @DeleteMapping(value = "/deleteTeam/{idTeam}")
    public void deleteTeam(@PathVariable("idTeam") Long idTeam)
    {
        teamService.deleteTeam(idTeam);
    }

    @GetMapping("/GetTeamById/{idTeam}")
    public Team findById(@PathVariable("idTeam") Long idTeam) {
        return teamService.findById(idTeam);
    }

}
