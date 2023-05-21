package com.example.projectmanagement.Reposirtory;


import com.example.projectmanagement.Domaine.Activity;
import com.example.projectmanagement.Domaine.Project;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {


    @Override
    Optional<Activity> findById(Long Long);
    @Query("SELECT a FROM Activity a WHERE a.project.id = :projectId AND a.project.projectManager.id = :projectManagerId")
    List<Activity> getActivityDetails(@Param("projectId") Long projectId, @Param("projectManagerId") Long projectManagerId);

    List<Activity> findByTeamId(Long TeamId);
    List<Activity> findByProjectId(Long projectId);

}
