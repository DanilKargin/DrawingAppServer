package com.example.demo.repository;

import com.example.demo.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WordRepository extends JpaRepository<Word, UUID> {
    @Query("SELECT w FROM words w WHERE TRIM(LOWER(w.term)) LIKE TRIM(LOWER(?1))")
    Optional<Word> findByTerm(String term);
    @Query("SELECT w FROM words w ORDER BY random() LIMIT 3")
    List<Word> getThreeRandomWord();
}
