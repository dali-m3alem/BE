package com.example.projectmanagement.Domaine;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
        private Long id;
        @Column(name = "team_name",nullable = false)
        private String teamName;
        private String teamDesc;
        @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
        @Column(name = "_activities")
        @JsonIgnore
        private List<Activity> activities = new ArrayList<>();

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
                name = "team_members",
                joinColumns = @JoinColumn(name = "team_id"),
                inverseJoinColumns = @JoinColumn(name = "user_id"))
        private List<User> members = new ArrayList<>();

        public void setMembers(List<User> members) {
            this.members = new ArrayList<>(members);}
        public void removeMember(User user) {
            members.remove(user);
        }

    }
