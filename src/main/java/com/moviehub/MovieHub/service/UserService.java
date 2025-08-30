package com.moviehub.MovieHub.service;

import com.moviehub.MovieHub.domain.Movie;
import com.moviehub.MovieHub.domain.User;
import com.moviehub.MovieHub.exception.ResourceNotFoundException;
import com.moviehub.MovieHub.repository.MovieRepository;
import com.moviehub.MovieHub.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    // Kullanıcı kayıt
    public User register(@Valid User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Bu email zaten kayıtlı: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    public User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + id));
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void addFavorite(Long userId, Long movieId) {
        User user = findUser(userId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Film bulunamadı: " + movieId));
        user.getFavoriteMovies().add(movie);
    }

    @Transactional
    public void removeFavorite(Long userId, Long movieId) {
        User user = findUser(userId);
        boolean removed = user.getFavoriteMovies().removeIf(m -> m.getId().equals(movieId));
        if (!removed) {
            throw new ResourceNotFoundException("Favorilerde böyle bir film yok: " + movieId);
        }
    }

    public Set<Movie> listFavorites(Long userId) {
        return findUser(userId).getFavoriteMovies();
    }
}
