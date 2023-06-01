package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.ActivityDto;
import com.example.projectmanagement.Domaine.Activity;

import java.util.List;

public interface ActitvtyServ {

    public Activity updateActivity(ActivityDto activityDto);
    public void deleteActivity(Long id);
    public Activity createActivity(ActivityDto activityDto);
    public Activity getActivityById(Long id);
    public List<Activity> getActivityByProjectId(Long projectId, Long userId);
    public List<String> getAllTeamMembersByActivityId(Long activityId);
    public List<Activity> getActivityByProjectId1(Long id);
}
