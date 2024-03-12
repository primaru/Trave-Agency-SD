package com.example.SD.controller;

import com.example.SD.model.Journey;
import com.example.SD.repository.JourneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class ClientController {

    @Autowired
    private JourneyRepository journeyRepository;

    @PostMapping("/selectJourney")
    public String selectJourney(HttpSession session, @RequestParam Long journeyId) {
        Journey selectedJourney = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid journey Id:" + journeyId));

        String transactionId = UUID.randomUUID().toString();
        session.setAttribute("transactionId", transactionId);
        session.setAttribute("selectedJourney", selectedJourney);

        return "redirect:/confirmJourney";
    }

}
