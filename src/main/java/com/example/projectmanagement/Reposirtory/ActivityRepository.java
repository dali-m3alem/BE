package com.example.projectmanagement.Reposirtory;


import com.example.projectmanagement.Domaine.Activity;
import com.example.projectmanagement.Domaine.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
     List<Activity> findByProjectId(Long projectId);
     List<Activity> findByProject(Project project);

    @Override
    Optional<Activity> findById(Long Long);

    @Query("SELECT a FROM Activity a WHERE a.project.id=?1 AND a.project.projectManager.id=?2")
    List<Activity> getActivityDetails(Long projectId, Long projectManagerId);
}
