package com.example.projectmanagement.Reposirtory;
import com.example.projectmanagement.Domaine.Task;
import com.example.projectmanagement.Domaine.User;
import org.hibernate.annotations.NamedQuery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    Optional<Task> findById(Long id);
    List<Task> findByUser(User user);
    List<Task> findByManager(User user);
    List<Task> findByUserIsNull();
    List<Task> findByUserId(Long userId);



}
