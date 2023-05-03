package com.example.projectmanagement.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Reposirtory.UserRepository;
import com.example.projectmanagement.Service.EmailSender;
import com.example.projectmanagement.Service.userImpService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.GeneralSecurityException;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class PasswordResetController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private userImpService passwordResetService;
    @Autowired
    private EmailSender emailSender;


    @PostMapping("/request")
    public ResponseEntity<?> resetPassword(@RequestParam String email) {
        try {
            passwordResetService.resetPassword(email);
            return ResponseEntity.ok("Password reset email sent");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending password reset email");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> showResetPasswordForm(@RequestParam("token") String token) {
        // Vérifier que le token est valide et qu'il correspond à un utilisateur dans la base de données
        User user = userRepository.findByResetToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid reset token");
        }

        // Créer une URI de redirection vers l'URL de reset-password avec le token dans les paramètres
        URI redirectUri = UriComponentsBuilder.fromUriString("http://localhost:4200/reset-password")
                .queryParam("token", token)
                .build()
                .toUri();

        // Retourner une réponse de redirection vers l'URI créée
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }



    @PostMapping("/reset-password")
    public ResponseEntity<?> processResetPasswordForm(@RequestParam("token") String token,
                                                      @RequestParam("password") String password) {
        // Vérifier que le token est valide et qu'il correspond à un utilisateur dans la base de données
        User user = userRepository.findByResetToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid reset token");
        }

        // Encoder le nouveau mot de passe
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);

        // Mettre à jour le mot de passe de l'utilisateur dans la base de données
        user.setPassword(encodedPassword);
        user.setResetToken(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successfully");
    }

}

