package com.moviehub.MovieHub.controller;

import com.moviehub.MovieHub.Dto.ReviewDto;
import com.moviehub.MovieHub.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movies")
@CrossOrigin(origins = "http://localhost:5500")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Bir filme ait yorumlar (herkes görür)
    @GetMapping("/{movieId}/reviews")
    public ResponseEntity<List<ReviewDto>> list(@PathVariable Long movieId) {
        return ResponseEntity.ok(reviewService.listByMovie(movieId));
    }

    // Ortalama puan + yorum sayısı
    @GetMapping("/{movieId}/rating")
    public ResponseEntity<Map<String, Object>> rating(@PathVariable Long movieId) {
        return ResponseEntity.ok(reviewService.movieRating(movieId));
    }

    // Yorum & puan oluştur/güncelle (aynı film için tek kayıt; upsert)
    // Body: { "rating": 9, "comment": "Harika!" }
    @PostMapping("/{movieId}/reviews/{userId}")
    public ResponseEntity<ReviewDto> upsert(@PathVariable Long movieId,
                                            @PathVariable Long userId,
                                            @Valid @RequestBody ReviewDto req) {
        ReviewDto dto = reviewService.upsertReview(userId, movieId, req);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // Yorum sil (kullanıcının kendi yorumu)
    @DeleteMapping("/{movieId}/reviews/{userId}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long movieId,
                                                      @PathVariable Long userId) {
        reviewService.deleteReview(userId, movieId);
        return ResponseEntity.ok(Map.of("message", "Yorum silindi"));
    }
}
