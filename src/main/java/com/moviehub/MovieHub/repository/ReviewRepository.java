package com.moviehub.MovieHub.repository;

import com.moviehub.MovieHub.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMovie_IdOrderByCreatedAtDesc(Long movieId);

    Optional<Review> findByUser_IdAndMovie_Id(Long userId, Long movieId);

    @Query("select avg(r.rating) from Review r where r.movie.id = :movieId")
    Double averageRating(Long movieId);

    @Query("select count(r) from Review r where r.movie.id = :movieId")
    Long countByMovieId(Long movieId);
}
