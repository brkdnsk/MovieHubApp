package com.moviehub.MovieHub.repository;

import com.moviehub.MovieHub.domain.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Arama: isim içinde geçenler (case-insensitive)
    Page<Movie> findByMovieNameContainingIgnoreCase(String q, Pageable pageable);

    // Filtre: tür (case-insensitive)
    Page<Movie> findByGenreIgnoreCase(String genre, Pageable pageable);

    Optional<Movie> findFirstByMovieNameIgnoreCase(String movieName);
}