package com.example.projectmanagement.Domaine;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_project")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Project implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "usr_seq")
    private Long id;
    private String projectName;
    private String descriptionP;
    private String objectiveP;
    private String durationP;
    private Date deadlineP;
    private String status;
    private Long budget;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonBackReference
    private User admin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_leader_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonBackReference
    private User projectManager;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Activity> activity = new ArrayList<>();
}