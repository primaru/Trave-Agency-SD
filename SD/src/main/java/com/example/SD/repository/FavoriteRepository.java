package com.example.SD.repository;

import com.example.SD.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByClientId(Long clientId);
    List<Favorite> findByClientIdAndJourneyId(Long clientId, Long journeyId);

}
