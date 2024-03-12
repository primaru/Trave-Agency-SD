package com.example.SD.repository;

import com.example.SD.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Custom queries can be added here if needed
}
