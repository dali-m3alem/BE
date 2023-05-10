package com.example.projectmanagement.Reposirtory;


import com.example.projectmanagement.Domaine.Team;
import com.example.projectmanagement.Domaine.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findById(Long idTeam);
    List<Team> findByMembersContaining(User user);

}
