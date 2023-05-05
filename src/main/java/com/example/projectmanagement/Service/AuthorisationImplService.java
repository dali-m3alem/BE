package com.example.projectmanagement.Service;

import com.example.projectmanagement.Domaine.Authorisation;
import com.example.projectmanagement.Reposirtory.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class AuthorisationImplService implements AuthServ {

    @Autowired
    private AuthRepository repository;

    public List<Authorisation> getAllRole() {

        return (List<Authorisation>) repository.findAll();
    }


    public Authorisation addrole(Authorisation authorisation) {
        return repository.save(authorisation);
    }

    public void deleteRole(Long idRole) {
        repository.deleteById(idRole);
    }


    public List<Authorisation> getRoleWSUN(String ch) {
        return repository.listUsers(ch);
    }
}
