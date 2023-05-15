package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.ActivityDto;
import com.example.projectmanagement.Domaine.Activity;

import java.util.List;

public interface ActitvtyServ {

    public List<Activity> getAllActivity();
    public List<Activity> getActivityByProjectId(Long id, Long ManagerId) throws Exception;
    public Activity updateActivity(ActivityDto activityDto);
    public void deleteActivity(Long id);
    public Activity createActivity(ActivityDto activityDto);
    public Activity getActivityById(Long id);
}
