package com.example.projectmanagement.Reposirtory;


import com.example.projectmanagement.Domaine.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByProjectId(Long projectId);

    @Override
    Optional<Activity> findById(Long Long);

}
