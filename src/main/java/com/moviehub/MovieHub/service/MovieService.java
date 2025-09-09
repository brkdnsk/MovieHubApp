package com.moviehub.MovieHub.service;

import com.moviehub.MovieHub.domain.Movie;
import com.moviehub.MovieHub.exception.ResourceNotFoundException;
import com.moviehub.MovieHub.repository.MovieRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // Controller bu ismi çağırıyor; aynı bırakıyoruz
    public List<Movie> getAllMoives() {
        return movieRepository.findAll();
    }

    public Movie createNewMovie(@Valid Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie findMovie(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id + " numaralı film bulunamadı."));
    }

    @Transactional
    public Movie updateMovie(Long id, @Valid Movie req) {
        Movie m = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id));

        // Alan alan güncelle
        m.setMovieName(req.getMovieName());
        m.setDescription(req.getDescription());
        m.setReleaseYear(req.getReleaseYear());
        m.setImdbRating(req.getImdbRating());
        m.setProducer(req.getProducer());
        m.setGenre(req.getGenre());
        m.setPosterUrl(req.getPosterUrl());
        m.setBackdropUrl(req.getBackdropUrl());
        m.setTrailerUrl(req.getTrailerUrl());
        m.setGallery(req.getGallery()); // String ise JSON string bekler

        // @Transactional nedeniyle flush ile persist edilir; yine de açıkça save edelim
        return movieRepository.save(m);
    }
}
