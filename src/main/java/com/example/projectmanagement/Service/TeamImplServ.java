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

import java.util.*;

@Service
@RequiredArgsConstructor
public class TeamImplServ implements TeamServ{
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    public List<Team> getAllTeam() {
        List<Team> teams = teamRepository.findAll();

        Set<Long> uniqueTeamIds = new HashSet<>();
        List<Team> uniqueTeams = new ArrayList<>();

        for (Team team : teams) {
            Long teamId = team.getId();
            if (!uniqueTeamIds.contains(teamId)) {
                uniqueTeamIds.add(teamId);
                List<User> uniqueMembers = getUniqueMembers(team.getMembers());
                team.setMembers(uniqueMembers);
                uniqueTeams.add(team);
            }
        }

        return uniqueTeams;
    }

    private List<User> getUniqueMembers(List<User> members) {
        Set<Long> uniqueUserIds = new HashSet<>();
        List<User> uniqueMembers = new ArrayList<>();

        for (User member : members) {
            Long userId = member.getId();
            if (!uniqueUserIds.contains(userId)) {
                uniqueUserIds.add(userId);
                uniqueMembers.add(member);
            }
        }

        return uniqueMembers;
    }



    public Team getTeamByActivityAndProjectAndManager(Long activityId, Long projectId, Long managerId) {
        Team team = teamRepository.getTeamByActivityAndProjectAndManager(activityId, projectId, managerId);

        if (team == null) {
            return null;
        }

        Set<Long> uniqueUserIds = new HashSet<>();
        List<User> uniqueMembers = new ArrayList<>();

        for (User member : team.getMembers()) {
            Long userId = member.getId();
            if (!uniqueUserIds.contains(userId)) {
                uniqueUserIds.add(userId);
                uniqueMembers.add(member);
            }
        }
        team.setMembers(uniqueMembers);
        return team;
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
        String teamName = teamRequest.getTeamName();
        Optional<Team> existingTeam = teamRepository.findByTeamName(teamName);
        Team teamToUpdate = teamRepository.findById(teamRequest.getTeamId()).orElseThrow(
                () -> new EntityNotFoundException("Team not found with the given teamId"));

        if (!teamRequest.getTeamName().equals(teamToUpdate.getTeamName()) && existingTeam.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team with the same name already exists.");
        }
        if (users.isEmpty()) {
            throw new EntityNotFoundException("No users found with given emails.");
        }

        Set<Long> uniqueUserIds = new HashSet<>();
        List<User> uniqueMembers = new ArrayList<>();

        for (User user : users) {
            Long userId = user.getId();
            if (!uniqueUserIds.contains(userId)) {
                uniqueUserIds.add(userId);
                uniqueMembers.add(user);
            }
        }

        teamToUpdate.setTeamName(teamRequest.getTeamName());
        teamToUpdate.setTeamDesc(teamRequest.getTeamDesc());
        teamToUpdate.setMembers(uniqueMembers);

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

    public Set<String> getAllTeamIds() {
        List<Team> teams = teamRepository.findAll();
        Set<String> teamIds = new HashSet<>();
        for (Team team : teams) {
            teamIds.add(team.getTeamName());
        }
        return teamIds;
    }
    public Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }



}
