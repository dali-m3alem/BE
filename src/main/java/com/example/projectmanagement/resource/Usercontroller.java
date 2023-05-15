package com.example.projectmanagement.resource;

import com.example.projectmanagement.DTO.*;
import com.example.projectmanagement.Domaine.Task;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Service.userImpService;
import com.example.projectmanagement.config.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class Usercontroller {


    @Autowired
    private userImpService service;
    private final JwtService serviceJWT;



    @PostMapping(value = "/register", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ResponseAuth> registerUser(@Valid @ModelAttribute RequestRegister request) throws IOException {
        try {
            ResponseAuth response = service.registerUser(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            return ResponseEntity.badRequest().body(new ResponseAuth(errorMessage));
        }
    }








    @PostMapping("/authenticate")
    public ResponseEntity<ResponseAuth> authenticate(

            @RequestBody RequestAuth request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }


    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping(value = "/createUserAndTask")
    public ResponseEntity<User> createUserAndTask(@RequestBody CreateUserAndTaskRequest request) {
        User user = request.getUser();
        Task task = request.getTask();
        if (user == null || task == null) {
            return ResponseEntity.badRequest().build();
        }
        service.createUserAndTask(user, task);
        return ResponseEntity.ok(user);
    }


    @PostMapping("/upload-profile-picture")
    public ResponseEntity<Map<String, String>> uploadProfilePicture(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) {
        service.uploadProfilePicture(file, userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile picture uploaded successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser) {
        try {
            User user =  service.updateUser(updatedUser);

            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("User not found");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred during the update process");
        }
    }

    @GetMapping("/countUser")
    public ResponseEntity<Long> countUser() {
        Long count = service.countUsers();
        return ResponseEntity.ok(count);
    }

    @PutMapping("/updateUserWP")
    public ResponseEntity<?> updateUserwp(@RequestBody User updatedUser) {
        try {
            User user = service.updateUserWP(updatedUser);
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("User not found");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public void changePassword(@RequestParam Long id, @RequestParam String oldPassword, @RequestParam String newPassword) {
        service.changePassword(id, oldPassword, newPassword);
    }






    @DeleteMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestParam Long id) {
        service.deleteUser(id);
        return ResponseEntity.ok().build();
    }




    @GetMapping(value = "/getAllUserBSUN/{str}")
    public List<User> getAllUserBSUN(@PathVariable("str") String str)

    {
        return service.getUserWSUN(str);
    }
    @GetMapping("/getuserId")
    public ResponseEntity<User> getUserById(@RequestParam Long id) {
        User user = service.getUserById(id);
        return ResponseEntity.ok(user);
    }



    @GetMapping(value = "/getAllUserBID/{userId}")
    public List<Task> getTasksForUser(@PathVariable Long userId) {
        return service.findAllTasksByUserId(userId);
    }



    @GetMapping("/withoutTasks")
    public ResponseEntity<List<User>> findAllUsersWithoutTasks() {
        List<User> users = service.findAllWithoutTasks();
        return ResponseEntity.ok(users);
    }
    @PostMapping("/roles")
    public ResponseEntity<?> addRoleToUser(@RequestBody Map<String, String> requestParams) {
        String username = requestParams.get("username");
        String roleName = requestParams.get("roleName");
        service.addRoleToUser(username, roleName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/managers")
    public List<String> getUsersWithManagerRole() {
        return service.getUsersWithManagerRole();

    }
    @GetMapping("/managersUsers")
    public List<String> getUsersWithManagerUserRole() {
        return service.getUsersWithManagerUserRole();

    }
}

