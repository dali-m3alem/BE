package com.example.projectmanagement.DTO;

import com.example.projectmanagement.Domaine.Task;
import com.example.projectmanagement.Domaine.User;
import lombok.Data;

@Data
public  class CreateUserAndTaskRequest {
    private User user;
    private Task task;
}
