package com.bryanbatanes.scrabble.model;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoresRepository extends JpaRepository<Scores, Long> {

    List<Scores> findAll(Sort sort);

}
