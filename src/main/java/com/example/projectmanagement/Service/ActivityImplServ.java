package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.ActivityDto;
import com.example.projectmanagement.Domaine.*;
import com.example.projectmanagement.Reposirtory.ActivityRepository;
import com.example.projectmanagement.Reposirtory.ProjectRepository;
import com.example.projectmanagement.Reposirtory.TaskRepository;
import com.example.projectmanagement.Reposirtory.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityImplServ implements ActitvtyServ{
    private final ActivityRepository activityRepository;

    private final ProjectRepository projectRepository;

    private final TeamRepository teamRepository;


    public ActivityImplServ(ActivityRepository activityRepository, ProjectRepository projectRepository, TeamRepository teamRepository, TaskRepository taskRepository) {
        this.activityRepository = activityRepository;
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
    }


    @Override
    public List<Activity> getAllActivity() {
        return activityRepository.findAll();
    }

        public List<Activity> getActivityByProjectId(Long id, Long managerId) {
        List<Activity> activities =  activityRepository
                .getActivityDetails(id, managerId);

        return activities;
    }

    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

        public Activity createActivity(ActivityDto activityDto) {
        Project project= projectRepository.findById(activityDto.getProjectId()).orElseThrow(()
                -> new IllegalArgumentException("Invalid project id"));
        Team team = teamRepository.findByTeamName(activityDto.getTeamName()).orElseThrow(()
                -> new IllegalArgumentException("Invalid team id"));
        Activity activity = new Activity();
        activity.setActivityName(activityDto.getActivityName());
        activity.setDescriptionA(activityDto.getDescriptionA());
        activity.setObjectiveA(activityDto.getObjectiveA());
        activity.setDeadlineA(activityDto.getDeadlineA());
        activity.setProject(project);
        activity.setTeam(team);

        return activityRepository.save(activity);
    }


    public Activity updateActivity(ActivityDto activityDto) {
        Activity activity = activityRepository.findById(activityDto.getId()).orElse(null);
        if (activity == null) {
            throw new EntityNotFoundException("Activity with id " + activityDto.getId() + " not found");
        }

        activity.setActivityName(activityDto.getActivityName());
        activity.setDescriptionA(activityDto.getDescriptionA());
        activity.setObjectiveA(activityDto.getObjectiveA());

        activity.setDeadlineA(activityDto.getDeadlineA());

        // set project and team
        Project project = new Project();
        project.setId(activityDto.getProjectId());
        activity.setProject(project);
        Team team = teamRepository.findByTeamName(activityDto.getTeamName())
                .orElseThrow(() -> new EntityNotFoundException("Team with name  not found"));

        team.setTeamName(activityDto.getTeamName());

        activity.setTeam(team);

        return activityRepository.save(activity);
    }



    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }
}
