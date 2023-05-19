package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.TaskDto;
import com.example.projectmanagement.DTO.TeamDTO;
import com.example.projectmanagement.Domaine.Activity;
import com.example.projectmanagement.Domaine.Project;
import com.example.projectmanagement.Domaine.Team;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Reposirtory.ActivityRepository;
import com.example.projectmanagement.Reposirtory.TeamRepository;
import com.example.projectmanagement.Reposirtory.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamImplServ implements TeamServ{
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivityRepository activityRepository;

    public List<Team> getAllTeam() {
        return teamRepository.findAll();
    }

    public Team getTeamByActivityAndProjectAndManager(Long activityId, Long projectId, Long managerId) {
        return teamRepository.getTeamByActivityAndProjectAndManager(activityId, projectId, managerId);
    }
    public Team addTeam(TeamDTO teamRequest) {
        String teamName = teamRequest.getTeamName();
        Optional<Team> existingTeam = teamRepository.findByTeamName(teamName);

        if (existingTeam.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team with the same name already exists.");
        }

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
        String teamName=teamRequest.getTeamName();
        Optional<Team> existingTeam = teamRepository.findByTeamName(teamName);
        Team teamToUpdate = teamRepository.findById(teamRequest.getTeamId()).get();

        if (!teamRequest.getTeamName().equals(teamToUpdate.getTeamName()) && existingTeam.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team with the same name already exists.");
        }
        if (users.isEmpty()) {
            throw new EntityNotFoundException("No users found with given emails.");
        }
        teamToUpdate.setTeamName(teamRequest.getTeamName());
        teamToUpdate.setTeamDesc(teamRequest.getTeamDesc());
        teamToUpdate.setMembers(users);
        return teamRepository.save(teamToUpdate);
    }
    //know we can delete team and st the activity team id null
    public void deleteTeam(Long idTeam) {
        Team team = teamRepository.findById(idTeam).orElseThrow(EntityNotFoundException::new);

        for (Activity activity : activityRepository.findByTeamId(idTeam)) {
            activity.setTeam(null);
            activityRepository.save(activity);
        }
        teamRepository.delete(team);
    }

    public List<String> getAllTeamIds() {
        List<Team> teams = teamRepository.findAll();
        List<String> teamIds = new ArrayList<>();
        for (Team team : teams) {
            teamIds.add(team.getTeamName());
        }
        return teamIds;
    }
    public Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }



}
