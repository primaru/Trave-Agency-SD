package com.example.SD.service;

import com.example.SD.model.Journey;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class TransactionService {

    public LocalDate processTransaction(Journey journey, String address, String transactionId) {

        return LocalDate.now().plusDays(3);
    }
}
