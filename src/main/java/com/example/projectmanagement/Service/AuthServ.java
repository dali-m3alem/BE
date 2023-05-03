package com.example.projectmanagement.Service;


import com.example.projectmanagement.Domaine.Authorisation;

import java.util.List;

public interface AuthServ {
    public List<Authorisation> getAllRole();
    public Authorisation addrole(Authorisation authorisation);
    public void deleteRole(Long idRole);
    public List<Authorisation> getRoleWSUN(String ch);
}
