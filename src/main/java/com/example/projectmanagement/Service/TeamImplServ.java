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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    //know we can delete team and st the activity team id null
    public void deleteTeam(Long idTeam) {
        Team team = teamRepository.findById(idTeam).orElseThrow(EntityNotFoundException::new);

        for (Activity activity : activityRepository.findByTeamId(idTeam)) {
            activity.setTeam(null);
            activityRepository.save(activity);
        }
        teamRepository.delete(team);
    }

    public List<Long> getAllTeamIds() {
        List<Team> teams = teamRepository.findAll();
        List<Long> teamIds = new ArrayList<>();
        for (Team team : teams) {
            teamIds.add(team.getId());
        }
        return teamIds;
    }
    public Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

}
