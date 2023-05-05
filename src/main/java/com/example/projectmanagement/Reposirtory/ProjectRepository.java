package com.example.projectmanagement.Reposirtory;


import com.example.projectmanagement.Domaine.Project;
import com.example.projectmanagement.Domaine.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Override
    Optional<Project> findById(Long Long);
    Optional<List<Project>> findByAdmin(User adminId);
    Optional<List<Project>> findByProjectManager(User managerId);
    @Query("SELECT p.projectManager FROM Project p WHERE p.id = :projectId")
    Long findActivityManagerIdByProjectId(@Param("projectId") Long projectId);


}
