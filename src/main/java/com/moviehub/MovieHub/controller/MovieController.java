package com.moviehub.MovieHub.controller;

import com.moviehub.MovieHub.domain.Movie;
import com.moviehub.MovieHub.repository.MovieRepository;
import com.moviehub.MovieHub.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movies")
@CrossOrigin(origins = "http://localhost:5500")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository; // ← search için gerekli

    @GetMapping
    public ResponseEntity<List<Movie>> ListAllMovies() {
        List<Movie> movies = movieService.getAllMoives();
        return ResponseEntity.ok(movies);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> createMovie(@Valid @RequestBody Movie movie) {
        movieService.createNewMovie(movie);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Film başarıyla eklendi");
        res.put("status", "true");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> createMovies(@RequestBody List<@Valid Movie> movies) {
        for (Movie m : movies) {
            movieService.createNewMovie(m);
        }
        Map<String, Object> res = new HashMap<>();
        res.put("message", movies.size() + "  tane film başarıyla eklendi");
        res.put("count", movies.size());
        res.put("status", true);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(movieService.findMovie(id));
    }

    // ---- Arama / Sayfalama / Sıralama ----
    @GetMapping("/search")
    public ResponseEntity<Page<Movie>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "movieName") String sortBy,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        Pageable pageable = PageRequest.of(
                page, size,
                "asc".equalsIgnoreCase(dir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
        );

        Page<Movie> result;
        if (q != null && !q.isBlank()) {
            result = movieRepository.findByMovieNameContainingIgnoreCase(q, pageable);
        } else if (genre != null && !genre.isBlank()) {
            result = movieRepository.findByGenreIgnoreCase(genre, pageable);
        } else {
            result = movieRepository.findAll(pageable);
        }

        return ResponseEntity.ok(result);
    }
}
