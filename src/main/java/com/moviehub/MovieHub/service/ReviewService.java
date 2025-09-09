package com.moviehub.MovieHub.service;

import com.moviehub.MovieHub.Dto.ReviewDto;
import com.moviehub.MovieHub.domain.Movie;
import com.moviehub.MovieHub.domain.Review;
import com.moviehub.MovieHub.domain.User;
import com.moviehub.MovieHub.exception.ResourceNotFoundException;
import com.moviehub.MovieHub.repository.MovieRepository;
import com.moviehub.MovieHub.repository.ReviewRepository;
import com.moviehub.MovieHub.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Transactional
    public ReviewDto upsertReview(Long userId, Long movieId, @Valid ReviewDto req) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));
        Movie m = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Film bulunamadı: " + movieId));

        Review r = reviewRepository.findByUser_IdAndMovie_Id(userId, movieId)
                .orElseGet(Review::new);

        r.setUser(u);
        r.setMovie(m);
        r.setRating(req.getRating());
        r.setComment(req.getComment());

        Review saved = reviewRepository.save(r);
        return ReviewDto.fromEntity(saved);
    }

    @Transactional
    public void deleteReview(Long userId, Long movieId) {
        Review r = reviewRepository.findByUser_IdAndMovie_Id(userId, movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Yorum bulunamadı"));
        reviewRepository.delete(r);
    }

    public List<ReviewDto> listByMovie(Long movieId) {
        return reviewRepository.findByMovie_IdOrderByCreatedAtDesc(movieId)
                .stream()
                .map(ReviewDto::fromEntity)
                .toList();
    }

    public Map<String, Object> movieRating(Long movieId) {
        Double avg = reviewRepository.averageRating(movieId);
        Long count = reviewRepository.countByMovieId(movieId);
        double safeAvg = avg == null ? 0.0 : Math.round(avg * 10.0) / 10.0; // 1 basamak
        return Map.of("movieId", movieId, "average", safeAvg, "count", count);
    }
}
