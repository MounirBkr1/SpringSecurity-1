package com.mnr.springsecurity.sec.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String username;
    //autoriser slm l ecriture=> pr qu'il soit pas affiché, le proteger contre la serialisation en format json
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    //EAGER: chaque fois que je charge un uses je dois charger ses roles +
    //initialiser collection a une arraylist vide
    @ManyToMany(fetch = FetchType.EAGER)

    private Collection<AppRole> appRoles = new ArrayList<>();
}
