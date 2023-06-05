package com.example.projectmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUser {
    private Long id;

    private String firstname;
    private String userLastName;

    private String email;

    private Long phoneNumber;

    private String titre;
    private String token;
}
