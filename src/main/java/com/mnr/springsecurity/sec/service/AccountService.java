package com.mnr.springsecurity.sec.service;

import com.mnr.springsecurity.sec.entities.AppRole;
import com.mnr.springsecurity.sec.entities.AppUser;

import java.util.List;

public interface AccountService {
    //add user
    AppUser addNewUser(AppUser appUser);

    AppRole addNewRole(AppRole appRole);

    void addRoleToUser(String username, String roleNAme);

    AppUser loadUserByUsername(String username);

    List<AppUser> listUsers();

}
