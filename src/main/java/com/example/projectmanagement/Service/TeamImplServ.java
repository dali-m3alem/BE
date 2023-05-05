package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.TaskDto;
import com.example.projectmanagement.DTO.TeamDTO;
import com.example.projectmanagement.Domaine.Project;
import com.example.projectmanagement.Domaine.Team;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Reposirtory.TeamRepository;
import com.example.projectmanagement.Reposirtory.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class TeamImplServ implements TeamServ{
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Team> getAllTeam() {
        return teamRepository.findAll();
    }

    public Team addTeam(TeamDTO teamRequest) {
        List<String> emailList = teamRequest.getEmails();
        List<User> users = userRepository.findAllByEmailIn(emailList);

        if (users.isEmpty()) {
            throw new EntityNotFoundException("No users found with given emails.");
        }

        Team team = new Team();
        team.setTeamName(teamRequest.getTeamName());
        team.setTeamDesc(teamRequest.getTeamDesc());
        team.setMembers(users);


        return teamRepository.save(team);
    }


    public Team updateTeam(TeamDTO teamRequest) {
        List<String> emailList = teamRequest.getEmails();
        List<User> users = userRepository.findAllByEmailIn(emailList);

        if (users.isEmpty()) {
            throw new EntityNotFoundException("No users found with given emails.");
        }
        Team teamToUpdate = teamRepository.findById(teamRequest.getTeamId()).get();
        teamToUpdate.setTeamName(teamRequest.getTeamName());
        teamToUpdate.setTeamDesc(teamRequest.getTeamDesc());
        teamToUpdate.setMembers(users);
        return teamRepository.save(teamToUpdate);
    }
    public void deleteTeam(Long idTeam) {
        // TODO Auto-generated method stub

        teamRepository.deleteById(idTeam);

    }


    public Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

}
