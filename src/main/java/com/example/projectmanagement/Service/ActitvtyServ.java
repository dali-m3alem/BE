package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.ActivityDto;
import com.example.projectmanagement.Domaine.Activity;

import java.util.List;

public interface ActitvtyServ {

    public Activity updateActivity(ActivityDto activityDto);
    public void deleteActivity(Long id);
    public Activity createActivity(ActivityDto activityDto);
    public Activity getActivityById(Long id);
    public List<Activity> getActivityByProjectId(Long id,Long user);
    public List<String> getAllTeamMembersByActivityId(Long activityId);
}
