package com.example.SD.controller;

import com.example.SD.repository.ClientRepository;
import com.example.SD.repository.FavoriteRepository;
import com.example.SD.repository.JourneyRepository;
import com.example.SD.model.Client;
import com.example.SD.model.Detail;
import com.example.SD.model.Favorite;
import com.example.SD.model.Journey;
import com.example.SD.service.DetailService;
import com.example.SD.service.JourneyService;
import com.example.SD.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class JourneyController {
    private static final Logger logger = LoggerFactory.getLogger(JourneyController.class);
    @Autowired
    private JourneyService journeyService;
    @Autowired
    private DetailService detailService;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private JourneyRepository journeyRepository;

    @GetMapping("/journeys/{id}")
    public String journeyDetail(@PathVariable Long id, Model model) {
        return journeyRepository.findById(id)
                .map(journey -> {
                    model.addAttribute("journey", journey);
                    return "journey-detail"; // This is the name of your Thymeleaf template
                })
                .orElse("redirect:/journeys"); // Redirect if the journey is not found
    }


    @GetMapping("/confirmJourney")
    public String confirmJourney(HttpSession session, Model model) {
        Journey selectedJourney = (Journey) session.getAttribute("selectedJourney");
        if (selectedJourney == null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("journey", selectedJourney);
        return "confirm-journey";
    }
    @PostMapping("/chooseJourney")
    public String chooseJourney(@RequestParam Long journeyId, HttpSession session) {
        Journey journey = journeyService.findById(journeyId);
        if (journey != null) {
            session.setAttribute("selectedJourney", journey);
            return "redirect:/confirmJourney";
        } else {
            // Handle the case where journey is not found
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/processTransaction")
    public String processTransaction(HttpSession session, @RequestParam String address, Model model) {
        Journey selectedJourney = (Journey) session.getAttribute("selectedJourney");
        String transactionId = (String) session.getAttribute("transactionId");

        if (selectedJourney == null || transactionId == null) {
            return "redirect:/dashboard";
        }

        LocalDate dispatchDate = transactionService.processTransaction(selectedJourney, address, transactionId);
        model.addAttribute("dispatchDate", dispatchDate);

        session.removeAttribute("transactionId");
        session.removeAttribute("selectedJourney");

        return "transactionSuccess";
    }
    @PostMapping("/favorites")

    public String handleFavorites(@RequestParam Long journeyId,
                                  @RequestParam String action,
                                  HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        logger.info("HANDLE");
        if (username == null) {
            // Handle the case where the user is not logged in
            logger.info("USER NULL");
            return "redirect:/login";
        }

        Client client = clientRepository.findByUsername(username);
        if (client == null) {
            // Handle the case where the client is not found
            logger.info("CLIENT NULL");
            return "redirect:/login";
        }


        Set<Favorite> favorites = (Set<Favorite>) session.getAttribute("favorites");
        if (favorites == null) {
            favorites = new HashSet<>();
        }

        if ("add".equals(action)) {
            List<Favorite> existingFavorites = favoriteRepository.findByClientIdAndJourneyId(client.getId(), journeyId);
            logger.info("FAVORITES NOT EMPTY "+existingFavorites);
            Journey journeyToAdd = journeyService.findById(journeyId);
            if (journeyToAdd == null) {
                // Handle the case where the journey is not found
                return "redirect:/dashboard";
            }
            boolean alreadyExists = false;
            for (Favorite fav : favorites) {
                Journey favJourney = journeyService.findById(fav.getJourneyId());
                if (favJourney != null && favJourney.getDestination().equals(journeyToAdd.getDestination())) {
                    alreadyExists = true;
                    break;
                }
            }
            if (existingFavorites.isEmpty() && !alreadyExists) {
                Favorite favorite = new Favorite();
                favorite.setJourneyId(journeyId);
                favorite.setClient(client);
                logger.info("FAVORITES EMPTY "+favorite);

                favoriteRepository.save(favorite);

                // Add the new favorite to the session favorites
                favorites.add(favorite);
            }
            else {
                // If the favorite already exists in the database, ensure the session is up to date
                favorites.addAll(existingFavorites);
            }
        } else if ("remove".equals(action)) {
            // Find and remove the favorite
            logger.info("REMOVE   ");

            Favorite favoriteToRemove = favorites.stream()
                    .filter(fav -> fav.getJourneyId().equals(journeyId))
                    .findFirst()
                    .orElse(null);
            if (favoriteToRemove != null) {
                logger.info("FAVORITE REMOVE");
                favoriteRepository.delete(favoriteToRemove);
                favorites.remove(favoriteToRemove);
            }
        }

        // Update the favorites in the session
        logger.info("favorites = "+favorites);
        session.setAttribute("favorites", favorites);

        return "redirect:/dashboard";
    }

    @GetMapping("/favorites")
    public String showFavorites(HttpSession session, Model model) {
        Set<Long> favoriteJourneyIds = new HashSet<>();

        Object favoritesObj = session.getAttribute("favorites");
        if (favoritesObj instanceof Collection<?>) {
            for (Object item : (Collection<?>) favoritesObj) {
                if (item instanceof Favorite) {
                    Favorite favorite = (Favorite) item;
                    favoriteJourneyIds.add(favorite.getJourneyId());
                }
            }
        }
        logger.info("FAVORITES OBJ= "+favoritesObj);
        logger.info("FAVORITES IDS= "+favoriteJourneyIds);
        // Fetch the journey details based on the journey IDs
        List<Journey> favoriteJourneys = favoriteJourneyIds.stream()
                .map(journeyService::findById)
                .filter(Objects::nonNull) // Ensure non-null values
                .collect(Collectors.toList());
        logger.info("Favorite journeys size: " + favoriteJourneys.size());

        model.addAttribute("favoriteJourneys", favoriteJourneys);
        return "favorites";
    }

    @PostMapping("/confirm-journey")
    public String handleJourneyConfirmation(@RequestParam String arrivalDate,
                                            @RequestParam String departureDate,
                                            HttpSession session, Model model) {
        // Retrieve the selected journey object from the session or from your service
        Journey selectedJourney = (Journey) session.getAttribute("selectedJourney");
        if (selectedJourney == null) {
            // Handle the case where the journey is not in the session
            logger.error("No selected journey in session");
            return "redirect:/dashboard"; // Redirect to a safe page
        }
        logger.info("CONFIRM JOURNEY REACHED");

        // Parse the arrival and departure dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Adjust pattern to match your date format
        LocalDate departureLocalDate = LocalDate.parse(departureDate, formatter);
        LocalDate arrivalLocalDate = LocalDate.parse(arrivalDate, formatter);

        // Compute the number of days between the two dates
        Period period = Period.between(arrivalLocalDate,departureLocalDate);

        // Generate a random detail for this journey
        Detail randomDetail = detailService.generateRandomDetail(selectedJourney);

        // Compute the total price based on the number of days and the price of the detail
        int days=period.getDays();
        int totalPrice = days * (int)randomDetail.getPrice();

        // Add computed details to the model
        model.addAttribute("journey", selectedJourney);
        model.addAttribute("arrivalDate", arrivalDate);
        model.addAttribute("departureDate", departureDate);
        model.addAttribute("detail", randomDetail); // Add the random detail to the model
        model.addAttribute("totalPrice", totalPrice); // Add the total price to the model

        // Redirect to the confirm-journey view
        return "confirm-journey";
    }



    private void updateRecentlyViewedCookie(HttpServletRequest request, HttpServletResponse response, String newJourneyId) {
        String recentlyViewed = Arrays.stream(request.getCookies())
                .filter(cookie -> "recentlyViewed".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("");

        String updatedRecentlyViewed = updateRecentlyViewed(recentlyViewed, newJourneyId, 5); // Keeping the last 5 viewed journeys

        Cookie recentlyViewedCookie = new Cookie("recentlyViewed", updatedRecentlyViewed);
        recentlyViewedCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days for cookie expiry
        recentlyViewedCookie.setHttpOnly(true);
        response.addCookie(recentlyViewedCookie);
    }

    private String updateRecentlyViewed(String current, String newJourney, int limit) {
        LinkedList<String> items = new LinkedList<>(Arrays.asList(current.split(",")));
        if (items.contains(newJourney)) {
            items.remove(newJourney);
        }
        items.addFirst(newJourney);
        while (items.size() > limit) {
            items.removeLast();
        }
        return String.join(",", items);
    }
}
