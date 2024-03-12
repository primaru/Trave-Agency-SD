package com.example.SD.repository;

import com.example.SD.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByUsername(String username);
}
