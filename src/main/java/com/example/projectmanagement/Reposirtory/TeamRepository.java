package com.example.projectmanagement.Reposirtory;


import com.example.projectmanagement.Domaine.Task;
import com.example.projectmanagement.Domaine.Team;
import com.example.projectmanagement.Domaine.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findById(Long idTeam);
    List<Team> findByMembersContaining(User user);
    Optional<Team>findByTeamName(String team);
    @Query("SELECT a.team FROM Activity a WHERE a.id = :activityId AND a.project.id = :projectId AND a.project.projectManager.id = :managerId")
    Team getTeamByActivityAndProjectAndManager(@Param("activityId") Long activityId, @Param("projectId") Long projectId, @Param("managerId") Long managerId);
}
