package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.TeamDTO;
import com.example.projectmanagement.Domaine.Team;

import java.util.List;

public interface TeamServ {
    public List<Team> getAllTeam();
    public Team addTeam(TeamDTO teamRequest);
    public Team updateTeam(TeamDTO teamRequest) ;
    public void deleteTeam(Long idTeam);
    public Team findById(Long id);
}
