package com.example.projectmanagement.Domaine;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authorisation")
public class Authorisation implements Serializable  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "auth_seq")
    @SequenceGenerator(name = "auth_seq",sequenceName = "auth_seq")
    private Long id;
    @Column(name = "role_name")
    private String RoleName;



}
