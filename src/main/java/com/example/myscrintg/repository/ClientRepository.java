package com.example.myscrintg.repository;

import com.example.myscrintg.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity,Long> {

    ClientEntity getClientEntityByIdClientTg(Long clientIdTg);

}
