package com.example.projectmanagement.resource;

import com.example.projectmanagement.DTO.TeamDTO;
import com.example.projectmanagement.Domaine.Team;
import com.example.projectmanagement.Service.TeamImplServ;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class TeamController {


    @Autowired
    private TeamImplServ teamService;
    @GetMapping(value = "/getAllTeam")
    public List<Team> getAllTeam() {
        return teamService.getAllTeam();
    }

    @PostMapping(value = "/addTeam")
    public Team addTeam(@RequestBody TeamDTO teamRequest)
    {
        return teamService.addTeam(teamRequest);
    }

    @PutMapping(value = "/updateTeam/{idTeam}")
    public Team updateTeam(@RequestBody  TeamDTO teamRequest)
    {
        return teamService.updateTeam(teamRequest);
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
