package com.example.SD.service;

import com.example.SD.model.Detail;
import com.example.SD.model.Journey;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class DetailService {

    public Detail generateRandomDetail(Journey selectedJourney) {
        long selectedJourneyId = selectedJourney.getId();
        int selectedJourneyInt=(int) selectedJourneyId;
        Detail detail = new Detail();
        detail.setId(selectedJourneyId);

        Random random = new Random();
        double minPrice = 0;
        double maxPrice = 0;

        switch (selectedJourneyInt%6) {
            case 1:
                minPrice = 10;
                maxPrice = 15;
                break;
            case 2:

                minPrice = 16;
                maxPrice = 20;
                break;
            case 3:

                minPrice = 12;
                maxPrice = 17;
                break;
            case 4:

                minPrice = 13;
                maxPrice = 15;
                break;
            case 5:

                minPrice = 9;
                maxPrice = 12;
                break;
            default:
                minPrice = 0;
                maxPrice = 1;
                break;
        }

        double price = minPrice + (maxPrice - minPrice) * random.nextDouble();
        detail.setPrice(price);

        String[] airlineNames = {"Airline 1", "Airline 2", "Airline 3"};
        int randomIndex = random.nextInt(airlineNames.length);
        String airline = airlineNames[randomIndex];
        detail.setAirline(airline);

        return detail;
    }
}