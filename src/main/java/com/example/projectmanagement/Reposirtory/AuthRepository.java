package com.example.projectmanagement.Reposirtory;


import com.example.projectmanagement.Domaine.Authorisation;
import com.example.projectmanagement.Domaine.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Authorisation,Long> {
    Optional<Authorisation> findById(Long id);



    @Query(value = "select * from authorisation a where a.role_name like :cle%",nativeQuery = true)
    List<Authorisation> listUsers(@Param("cle") String role_name);

    @Query(value = "select * from authorisation a where a.role_name like :cle",nativeQuery = true)
    Authorisation role(@Param("cle") String role_name);
}
