package com.example.projectmanagement.Reposirtory;


import com.example.projectmanagement.Domaine.Activity;
import com.example.projectmanagement.Domaine.Task;
import com.example.projectmanagement.Domaine.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByProjectId(Long projectId);

    @Override
    Optional<Activity> findById(Long Long);
    List<Activity> findByTeamId(Long TeamId);
    @Query("SELECT t FROM Task t JOIN t.activity a WHERE a.id = :activityId")
    List<Task> findAllTasksByActivityId(@Param("activityId") Long activityId);




}
