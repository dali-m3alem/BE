package com.example.projectmanagement.Reposirtory;


import com.example.projectmanagement.Domaine.Task;
import com.example.projectmanagement.Domaine.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Transactional
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User>findById(Long id);

    Optional<User>findByEmail(String email);

    @Query(value = "select * from _user u where u.username like :cle%",nativeQuery = true)
    List<User> listUsers(@Param("cle") String username);
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId")
    List<Task> findAllTasksByUserId(@Param("userId") Long userId);
    @Query("SELECT u FROM User u")
    List<User> findAllWithoutTasks();
    void deleteById(Long id);
    User findByResetToken(String resetoken);
    List<User> findAllByEmailIn(List<String> emails);

}
