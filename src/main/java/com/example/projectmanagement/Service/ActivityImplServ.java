package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.ActivityDto;
import com.example.projectmanagement.Domaine.*;
import com.example.projectmanagement.Reposirtory.ActivityRepository;
import com.example.projectmanagement.Reposirtory.ProjectRepository;
import com.example.projectmanagement.Reposirtory.TaskRepository;
import com.example.projectmanagement.Reposirtory.TeamRepository;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityImplServ implements ActitvtyServ {
    private final ActivityRepository activityRepository;

    private final ProjectRepository projectRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final TeamRepository teamRepository;
    private final TaskServ taskServ;
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Activity> getAllActivity() {
        return activityRepository.findAll();
    }

    public Long getActivityManagerId(Long projectId) {
        return projectRepository.findActivityManagerIdByProjectId(projectId);
    }

    public List<Activity> getActivityByProjectId(Long id) {
        return activityRepository.findByProjectId(id);
    }

    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity not found"));
    }

    public Activity createActivity(ActivityDto activityDto) {
        Project project = projectRepository.findById(activityDto.getProjectId()).orElseThrow(()
                -> new IllegalArgumentException("Invalid project id"));
        Team team = teamRepository.findById(activityDto.getTeamId()).orElseThrow(()
                -> new IllegalArgumentException("Invalid team id"));
        Activity activity = new Activity();
        activity.setActivityName(activityDto.getActivityName());
        activity.setDescriptionA(activityDto.getDescriptionA());
        activity.setObjectiveA(activityDto.getObjectiveA());
        activity.setDurationA(activityDto.getDurationA());
        activity.setDeadlineA(activityDto.getDeadlineA());
        activity.setStatus("not started");
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
        activity.setDurationA(activityDto.getDurationA());
        activity.setDeadlineA(activityDto.getDeadlineA());

        // set project and team
        Project project = new Project();
        project.setId(activityDto.getProjectId());
        activity.setProject(project);

        Team team = new Team();
        team.setId(activityDto.getTeamId());
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
        return team.getMembers().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
    }
    public String changeActivityStatus(Long activityId) {
        Activity activity = getActivityById(activityId);
        List<Task> tasks = taskServ.getTaskByActivityId(activityId);
        boolean allTasksDone = true;
        for (Task task : tasks) {
            if (!task.getStatus().equals("DONE")) {
                allTasksDone = false;
                break;
            }
        }
        if (allTasksDone == true) {
            activity.setStatus("DONE");
        }activityRepository.save(activity);
        return activity.getStatus();

    }
    //STATISTIC OF ACTIVITIES
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

}


