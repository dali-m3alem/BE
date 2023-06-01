package com.example.projectmanagement.Service;

import com.example.projectmanagement.DTO.RequestAuth;
import com.example.projectmanagement.DTO.RequestRegister;
import com.example.projectmanagement.DTO.ResponseAuth;
import com.example.projectmanagement.Domaine.*;
import com.example.projectmanagement.Reposirtory.*;
import com.example.projectmanagement.config.JwtService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class userImpService implements UserSer{
    private final UserRepository repository;
    private final JwtService serviceJWT;
    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    @Autowired
    private AuthRepository repositoryAu;
    private TaskImplServ TaskImplServ;
    private final AuthenticationManager authenticationManager;
    private final EntityManagerFactory entityManagerFactory;
    private final PasswordEncoder passwordEncoder;
    private final ProjectRepository projectRepository;



    public Long countUsers() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query query = entityManager.createQuery("SELECT COUNT(p) FROM User p");
        Long count = (Long) query.getSingleResult();
        entityManager.close();
        return count;
    }
    public List<User> getAllUsers() {
        List<User> users = repository.findAll();
        if(users.isEmpty()){
            throw new RuntimeException("No users found");
        }
        return users;
    }
    public User addUser(User user) {
        // TODO Auto-generated method stub
        return repository.save(user);
    }
    public ResponseAuth authenticate(RequestAuth request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                 )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = serviceJWT.generateToken(user);
        return ResponseAuth.builder()
                .token(jwtToken)
                .build();
    }
    public void uploadProfilePicture(MultipartFile file, Long userId) {
        User user = repository.findById(userId).orElse(null);
        if (user != null) {
            try {
                user.setProfilePicture(file.getBytes());
                repository.save(user);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to read file", e);
            }
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
    @Override
    public ResponseAuth registerUser(RequestRegister request) throws IOException {
        String roleName = request.getRoleName();
        // Vérifier si le rôle existe
        Authorisation role = repositoryAu.role(roleName);
        final int MAX_PROFILE_PICTURE_SIZE = 1048576; // 5 MB in bytes

        // Vérifier si le fichier image est fourni
        byte[] profilePicture = null;
        if (request.getProfilePicture() != null) {
                try {
                    profilePicture = request.getProfilePicture().getBytes();
                }catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to read profile picture file", e);
            }
        }
        // Vérifier si l'email existe déjà
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        // Créer un nouvel utilisateur avec son rôle correspondant
        User user = User.builder()
                .firstname(request.getFirstname())
                .userLastName(request.getUserLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .profilePicture(profilePicture)
                .titre(request.getTitre())
                .roles(Collections.singleton(role))
                .build();

        // Enregistrer le nouvel utilisateur
        User savedUser = repository.save(user);

        // Générer un jeton JWT pour l'utilisateur
        var jwtToken = serviceJWT.generateToken(savedUser);

        // Retourner la réponse contenant le jeton JWT
        return ResponseAuth.builder()
                .token(jwtToken)
                .build();
    }








    public void createUserAndTask(User user, Task task) {
        if (user == null || task == null) {
            throw new IllegalArgumentException("User and task must not be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        User savedUser = repository.save(user);
        task.setUser(savedUser);
        taskRepository.save(task);
    }
    @Override
    public User updateUserWP(User updatedUser) {
        User user = repository.findById(updatedUser.getId())
                .orElseThrow(EntityNotFoundException::new);
        if (!updatedUser.getEmail().equals(user.getEmail())
                && repository.findByEmail(updatedUser.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        // mettre à jour les autres champs de l'utilisateur
        user.setFirstname(updatedUser.getFirstname());
        user.setUserLastName(updatedUser.getUserLastName());
        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setTitre(updatedUser.getTitre());
        return repository.save(user);
    }

    @Override
    public void  changePassword(Long id, String oldPassword, String newPassword) {
        User user = repository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
    }
    public class InvalidPasswordException extends RuntimeException {
        public InvalidPasswordException(String message) {
            super(message);
        }
    }


    @Override
    public User updateUser(User updatedUser) {
        User user = repository.findById(updatedUser.getId())
                .orElseThrow(EntityNotFoundException::new);
        if (!updatedUser.getEmail().equals(user.getEmail())
                && repository.findByEmail(updatedUser.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        // Update other user fields
        user.setFirstname(updatedUser.getFirstname());
        user.setUserLastName(updatedUser.getUserLastName());
        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setTitre(updatedUser.getTitre());
        user.setRoles(updatedUser.getRoles());

        return repository.save(user);
    }
    @Transactional
    public void deleteUser(Long id) {
        User user = repository.findById(id).orElseThrow(EntityNotFoundException::new);

        // Remove the user from any teams they belong to
        List<Team> teams = teamRepository.findByMembersContaining(user);
        for (Team team : teams) {
            team.getMembers().remove(user);
            teamRepository.save(team);
        }

        // Remove the user from any tasks they are assigned to
        List<Task> userTasks = taskRepository.findByUser(user);
        for (Task task : userTasks) {
            task.setUser(null);
            taskRepository.save(task);
        }

        List<Task> userTask = taskRepository.findByManager(user);
        for (Task task : userTask) {
            task.setManager(null);
            taskRepository.save(task);
        }

        // Remove the admin from any project they are assigned to
        List<Project> adminProject = projectRepository.findByAdmin(user);
        for (Project project : adminProject) {
            project.setAdmin(null);
            projectRepository.save(project);
        }

        // Remove the manager from any project they are assigned to
        List<Project> managerProject = projectRepository.findByProjectManager(user);
        for (Project project : managerProject) {
            project.setProjectManager(null);
            projectRepository.save(project);
        }

        // Delete the user from the database
        repository.delete(user);
    }



    @Transactional
    public User getUserById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        user.getTasks();
        Hibernate.initialize(user.getRoles());
        return user;
    }


    public List<User> getUserWSUN(String ch) {
        // TODO Auto-generated method stub
        return repository.listUsers(ch);
    }


    public List<Task> findAllTasksByUserId(Long userId) {
        return repository.findAllTasksByUserId(userId);
    }


    public List<User> findAllWithoutTasks() {
        return repository.findAllWithoutTasks();
    }
    public void addRoleToUser(String email, String roleName) {
        User user = repository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Vérifier si le rôle existe
       Authorisation role = repositoryAu.role(roleName);
        user.getRoles().add(role);
        repository.save(user);
    }

    @Autowired
    private EmailSender emailSender;

    public void resetPassword(String email) throws MessagingException, GeneralSecurityException {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));
        String resetToken = generateResetToken();
        user.setResetToken(resetToken); // Enregistrer le token avec l'utilisateur
        repository.save(user);
        String subject = "Password Reset";
        String body = "To reset your password, click on the following link: "
                + "http://localhost:4200/reset-password?token=" + resetToken; // Envoyer le lien de réinitialisation de mot de passe par e-mail
        emailSender.sendEmail(email, subject, body);
    }

    public static String generateResetToken() {
        String token = UUID.randomUUID().toString();
        return token.replaceAll("-", "");
    }



    public List<String> getUsersWithManagerRole() {
        List<User> usersWithManagerUserRole = repository.findUsersByRoleName("manager");
        List<String> emails = new ArrayList<>();
        for (User user : usersWithManagerUserRole) {
            emails.add(user.getEmail());
        }
        return emails;
    }
    public List<String> getUsersWithManagerUserRole() {
        List<User> usersWithManagerUserRole = repository.findUsersByRoleName("manager");
        List<User> usersWithManagerUserRole1 = repository.findUsersByRoleName("user");

        List<String> emails = new ArrayList<>();
        for (User user : usersWithManagerUserRole) {
            emails.add(user.getEmail());
        }
        for (User user : usersWithManagerUserRole1) {
            emails.add(user.getEmail());
        }
        return emails;
    }
   /* public List<User> findMembersByTeamId(Long userId) {
            Optional<User> Team = repository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return new ArrayList<>(user.getTeams().stream()
                        .flatMap(team -> team.getMembers().stream())
                        .collect(Collectors.toSet()));
            }
            return Collections.emptyList();
        }*/
    public List<User> findUsersByIds(List<Long> userIds) {
        return repository.findAllById(userIds);
    }
    public User findUserById(Long userId) {
        Optional<User> userOptional = repository.findById(userId);
        return userOptional.orElse(null);
    }
    public Long countTotalUsers() {
        return repository.count();
    }

    public Long countUsersWithTasks() {
        return repository.countUsersWithTasks();
    }
    public double calculateTaskPercentage() {
        Long totalUsers = countTotalUsers();
        Long usersWithTasks = countUsersWithTasks();

        if (totalUsers == 0) {
            return 0.0;
        }

        return (usersWithTasks.doubleValue() / totalUsers.doubleValue()) * 100.0;
    }
    public Long countUserWithNoTask(){
        Long AllUsers = countTotalUsers();
        Long UsersWithTask = countUsersWithTasks();
        return AllUsers-UsersWithTask;
    }
    public double calculateNoTaskPercentage() {
        Long totalUsers = countTotalUsers();
        Long usersWithNoTasks = countUserWithNoTask();

        if (totalUsers == 0) {
            return 0.0;
        }

        return (usersWithNoTasks.doubleValue() / totalUsers.doubleValue()) * 100.0;
    }


}



