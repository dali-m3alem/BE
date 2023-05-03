package com.example.projectmanagement.resource;


import com.example.projectmanagement.Domaine.Authorisation;
import com.example.projectmanagement.Service.AuthorisationImplService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RoleController {
@Autowired
private AuthorisationImplService service;


    @PostMapping(value = "/addrole")
    public Authorisation addrole(@RequestBody Authorisation authorisation)
    {return service.addrole(authorisation);
    }



    @DeleteMapping(value = "/deleteRole/{idu}")
    public void deleteRole(@PathVariable("idu") Long idUser)
    {
        service.deleteRole(idUser);
    }

    @GetMapping(value = "/getAllRole")
    // @PreAuthorize("hasRole('ADMIN')")
    public List<Authorisation> getAllRole() {
        return service.getAllRole();

    }
    @GetMapping(value = "/getRoleWSUN/{str}")
    public List<Authorisation> getRoleWSUN(@PathVariable("str") String str)
    {return service.getRoleWSUN(str);
    }


}
