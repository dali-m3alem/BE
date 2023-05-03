package com.example.projectmanagement.Reposirtory;


import com.example.projectmanagement.Domaine.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findById(Long idTeam);
}
