package com.spbmi.tracker.exercise.repo;

import com.spbmi.tracker.exercise.model.WeightEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeightEntryRepository extends JpaRepository<WeightEntry, Long> {

    List<WeightEntry> findAllByOrderByLoggedOnDesc();

    Optional<WeightEntry> findByLoggedOn(LocalDate loggedOn);

    List<WeightEntry> findByLoggedOnBetweenOrderByLoggedOnAsc(LocalDate startInclusive, LocalDate endInclusive);
}
