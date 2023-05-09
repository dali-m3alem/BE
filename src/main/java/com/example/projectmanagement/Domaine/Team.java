package com.example.projectmanagement.Domaine;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_Team")
public class Team implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "team_seq")
    @SequenceGenerator(name = "team_seq",sequenceName = "team_seq")
    private Long TeamId;
    @Column(name = "team_name")
    private String TeamName;
    private String TeamDesc;
    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    @Column(name = "_activities")
    private Set<Activity> activities = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members = new HashSet<>();


    public void setMembers(List<User> members) {
        this.members = new HashSet<>(members);
    }

    public void removeMember(User user) {
        members.remove(user);
    }

}






