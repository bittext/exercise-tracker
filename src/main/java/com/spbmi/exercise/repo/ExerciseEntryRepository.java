package com.spbmi.exercise.repo;

import com.spbmi.exercise.model.ExerciseEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExerciseEntryRepository extends JpaRepository<ExerciseEntry, Long> {

    List<ExerciseEntry> findAllByOrderByLoggedOnDescIdDesc();

    List<ExerciseEntry> findByLoggedOnOrderByIdAsc(LocalDate loggedOn);

    List<ExerciseEntry> findByLoggedOnBetweenOrderByLoggedOnAscIdAsc(LocalDate startInclusive, LocalDate endInclusive);
}
