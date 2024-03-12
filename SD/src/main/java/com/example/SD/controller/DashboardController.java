package com.example.SD.controller;

import com.example.SD.repository.ClientRepository;
import com.example.SD.repository.FavoriteRepository;
import com.example.SD.service.JourneyService;
import com.example.SD.repository.FeedbackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private JourneyService journeyService;

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private HttpSession session;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            model.addAttribute("journeys", journeyService.getAllJourneys());
            model.addAttribute("feedbacks", feedbackRepository.findAll());

            Set<Long> favorites;
            Object favoritesObj = session.getAttribute("favorites");
            if (favoritesObj instanceof Set) {
                favorites = (Set<Long>) favoritesObj;
                logger.info("(Set<Long>) favoritesObj");
            } else if (favoritesObj instanceof List) {
                favorites = new HashSet<>((List<Long>) favoritesObj);
                logger.info("(List<Long>) favoritesObj");
            } else {
                favorites = new HashSet<>();
            }
            model.addAttribute("favorites", favorites);
        }

        return "dashboard";
    }




}
