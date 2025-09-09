package com.moviehub.MovieHub.controller;

import com.moviehub.MovieHub.domain.Actor;
import com.moviehub.MovieHub.domain.Movie;
import com.moviehub.MovieHub.repository.ActorRepository;
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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movies")
@CrossOrigin(origins = "http://localhost:5500")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository; // arama/sayfalama için

    @Autowired
    private ActorRepository actorRepository; // <-- cast yönetimi için

    @GetMapping
    public ResponseEntity<List<Movie>> ListAllMovies() {
        List<Movie> movies = movieService.getAllMoives(); // mevcut isimle bıraktım
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

    // ---- ID'ye göre GÜNCELLEME ----
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Movie> updateMovie(
            @PathVariable("id") Long id,
            @Valid @RequestBody Movie body
    ) {
        Movie updated = movieService.updateMovie(id, body);
        return ResponseEntity.ok(updated);
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

    // =====================================================================
    //                          CAST (OYUNCU) YÖNETİMİ
    // =====================================================================

    /** Filmin mevcut cast'ini getir (sade isim listesi). */
    @GetMapping("/{movieId}/cast")
    public ResponseEntity<Map<String, Object>> getCast(@PathVariable Long movieId) {
        Movie movie = movieService.findMovie(movieId);
        List<String> names = movie.getCast()
                .stream()
                .map(Actor::getName)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "movieId", movieId,
                "count", names.size(),
                "actors", names
        ));
    }

    /**
     * İSİMLE oyuncu ekleme.
     * Body: ["Leonardo DiCaprio","Joseph Gordon-Levitt"]
     * İsim yoksa actor oluşturulur; varsa reuse edilir.
     */
    @PostMapping(path = "/{movieId}/cast", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> addCastByNames(@PathVariable Long movieId,
                                                              @RequestBody List<String> actorNames) {
        Movie movie = movieService.findMovie(movieId);

        int added = 0;
        for (String raw : actorNames == null ? Collections.<String>emptyList() : actorNames) {
            if (raw == null) continue;
            String name = raw.trim();
            if (name.isBlank()) continue;

            Actor actor = actorRepository.findFirstByNameIgnoreCase(name)
                    .orElseGet(() -> actorRepository.save(new Actor(null, name)));

            if (movie.getCast().add(actor)) {
                added++;
            }
        }
        movieService.createNewMovie(movie); // kaydet

        return ResponseEntity.ok(Map.of(
                "movieId", movieId,
                "added", added,
                "totalCast", movie.getCast().size(),
                "message", "Oyuncular eklendi"
        ));
    }

    /** ID ile oyuncu ekleme: /movies/{movieId}/cast/{actorId} */
    @PostMapping("/{movieId}/cast/{actorId}")
    public ResponseEntity<Map<String, Object>> addCastById(@PathVariable Long movieId,
                                                           @PathVariable Long actorId) {
        Movie movie = movieService.findMovie(movieId);
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new NoSuchElementException("Actor not found: " + actorId));

        boolean added = movie.getCast().add(actor);
        movieService.createNewMovie(movie);

        return ResponseEntity.ok(Map.of(
                "movieId", movieId,
                "actorId", actorId,
                "added", added
        ));
    }

    /** Cast'ten aktör çıkarma: /movies/{movieId}/cast/{actorId} */
    @DeleteMapping("/{movieId}/cast/{actorId}")
    public ResponseEntity<Map<String, Object>> removeCast(@PathVariable Long movieId,
                                                          @PathVariable Long actorId) {
        Movie movie = movieService.findMovie(movieId);
        boolean removed = movie.getCast().removeIf(a -> Objects.equals(a.getId(), actorId));
        movieService.createNewMovie(movie);

        return ResponseEntity.ok(Map.of(
                "movieId", movieId,
                "actorId", actorId,
                "removed", removed
        ));

    }
}
