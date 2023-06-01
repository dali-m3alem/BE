package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.ActivityDto;
import com.example.projectmanagement.DTO.ProjectDto;
import com.example.projectmanagement.Domaine.*;
import com.example.projectmanagement.Reposirtory.ActivityRepository;
import com.example.projectmanagement.Reposirtory.ProjectRepository;
import com.example.projectmanagement.Reposirtory.TaskRepository;
import com.example.projectmanagement.Reposirtory.TeamRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import jakarta.persistence.Query;


import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ActivityImplServ implements ActitvtyServ{
    private final ActivityRepository activityRepository;
    private final ProjectRepository projectRepository;
    private final TaskServ taskServ;
    private final TeamRepository teamRepository;
    private final EntityManager entityManager;
    public List<Activity> getAllActivity() {
        return activityRepository.findAll();
    }

    public List<Activity> getActivityByProjectId(Long projectId, Long userId) {
        List<Activity> activities = activityRepository.getActivityDetails(projectId, userId);

        for (Activity activity : activities) {
            List<Task> tasks = taskServ.getTaskByActivityId(activity.getId());
            boolean allTasksDone = true;

            if (tasks.isEmpty()) {
                activity.setStatus("not started");
            } else {
                for (Task task : tasks) {
                    if (!task.getStatus().equals("done")) {
                        allTasksDone = false;
                        break;
                    }
                }
                if (allTasksDone) {
                    activity.setStatus("done");
                } else {
                    activity.setStatus("in progress");
                }
            }
            activityRepository.save(activity);

        }

        return activities;
    }

    public List<Activity> getActivityByProjectId1(Long id) {
        return activityRepository.findByProjectId(id);
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
activity.setStatus("not started");

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
    @Override
    public List<String> getAllTeamMembersByActivityId(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NoSuchElementException("Activity not found"));

        Team team = activity.getTeam();
        if (team == null) {
            throw new IllegalStateException("No team assigned to the activity");
        }

        Set<String> uniqueEmails = new HashSet<>();
        List<String> teamMemberEmails = new ArrayList<>();

        for (User user : team.getMembers()) {
            String email = user.getEmail();
            if (!uniqueEmails.contains(email)) {
                uniqueEmails.add(email);
                teamMemberEmails.add(email);
            }
        }

        return teamMemberEmails;
    }

    public String changeProjectStatus(Long id) {
        Activity activity = getActivityById(id);
        List<Task> tasks = taskServ.getTaskByActivityId(id);
        boolean allActivitiesDone = true;

        if (tasks.isEmpty()) {
            activity.setStatus("not started");
        } else {
            for (Task task : tasks) {
                if (!activity.getStatus().equals("done")) {
                    allActivitiesDone = false;
                    break;
                }
            }
            if (allActivitiesDone) {
                activity.setStatus("done");
            } else {
                activity.setStatus("in progress");
            }
        }

        activityRepository.save(activity);
        return activity.getStatus();
    }
    public Long countActivity() {
        return  activityRepository.count();
    }
    public Long countActivitiesByStatus(String status) {
        Query query = createCountQueryByStatus(status);
        Long count = (Long) query.getSingleResult();
        return count;
    }
    private Query createCountQueryByStatus(String status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Activity> root = query.from(Activity.class);

        query.select(cb.count(root));
        query.where(cb.equal(root.get("status"), status));

        return entityManager.createQuery(query);
    }

    public Long countactvi() {
        return activityRepository.count();
    }
}
