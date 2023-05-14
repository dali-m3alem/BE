package com.example.projectmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestRegister {
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Long phoneNumber;
    private String RoleName;
    private MultipartFile profilePicture;
    private String titre;
}
