package com.example.projectmanagement.Service;


import com.example.projectmanagement.DTO.RequestAuth;
import com.example.projectmanagement.DTO.RequestRegister;
import com.example.projectmanagement.DTO.ResponseAuth;
import com.example.projectmanagement.Domaine.Task;
import com.example.projectmanagement.Domaine.User;

import java.io.IOException;
import java.util.List;

public interface UserSer {
    public List<User> getAllUsers();
    public ResponseAuth authenticate(RequestAuth request);
    public void createUserAndTask(User user, Task task);
    public User addUser(User user);

    User updateUserWP(User updatedUser);
    public User updateUser(User updatedUser);
    public void deleteUser(Long id);
    public List<User> getUserWSUN(String ch);
    public List<User> findAllWithoutTasks();
    public void changePassword(Long userId, String oldPassword, String newPassword);
    public ResponseAuth registerUser(RequestRegister request) throws IOException;
    public User getUserById(Long id);
    public void addRoleToUser(String email, String roleName);
    List<User> findUsersByIds(List<Long> userIds);
    public User findUserById(Long userId);
    public Long countTotalUsers();
    public Long countUsersWithTasks();
    public double calculateTaskPercentage();
}
