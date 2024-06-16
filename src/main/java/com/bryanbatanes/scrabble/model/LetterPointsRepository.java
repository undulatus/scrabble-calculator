package com.bryanbatanes.scrabble.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LetterPointsRepository extends JpaRepository<LetterPoints, Long> {

    @Query("SELECT DISTINCT lp.points FROM LetterPoints lp")
    public Set<Integer> findDistinctPoints();

    public List<LetterPoints> findByPoints(Integer points);
}
