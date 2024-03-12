package com.example.SD.service;

import com.example.SD.model.Journey;
import com.example.SD.repository.JourneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JourneyService {

    private final JourneyRepository journeyRepository;
    private static final Logger logger = LoggerFactory.getLogger(JourneyService.class);

    @Autowired
    public JourneyService(JourneyRepository journeyRepository) {
        this.journeyRepository = journeyRepository;
    }

    public List<Journey> getAllJourneys() {
        List<Journey> journeys = journeyRepository.findAll();
        logger.info("Number of journeys found: {}", journeys.size());
        return journeys;
    }
    public Journey findById(Long id) {
        return journeyRepository.findById(id).orElse(null);
    }


}
