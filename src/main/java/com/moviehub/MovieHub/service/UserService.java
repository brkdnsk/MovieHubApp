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

    // Basit login (şimdilik düz metin parola)
    public User login(String email, String password) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email bulunamadı: " + email));
        if (!u.getPassword().equals(password)) {
            throw new IllegalArgumentException("Şifre hatalı");
        }
        return u;
    }

    // --------- İZLENENLER (watched) ---------

    @Transactional
    public void addWatched(Long userId, Long movieId) {
        User u = findUser(userId);
        Movie m = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Film bulunamadı: " + movieId));
        u.getWatchedMovies().add(m);
    }

    @Transactional
    public void removeWatched(Long userId, Long movieId) {
        User u = findUser(userId);
        boolean removed = u.getWatchedMovies().removeIf(m -> m.getId().equals(movieId));
        if (!removed) {
            throw new ResourceNotFoundException("İzlenenlerde böyle bir film yok: " + movieId);
        }
    }

    public Set<Movie> listWatched(Long userId) {
        return findUser(userId).getWatchedMovies();
    }

    public boolean isWatched(Long userId, Long movieId) {
        return findUser(userId).getWatchedMovies()
                .stream()
                .anyMatch(m -> m.getId().equals(movieId));
    }

    /**
     * Toggle: İzlenmediyse ekler, izlendiyse kaldırır.
     * @return true => şimdi izlendi; false => işaret kaldırıldı
     */
    @Transactional
    public boolean toggleWatched(Long userId, Long movieId) {
        User u = findUser(userId);
        Movie m = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Film bulunamadı: " + movieId));
        boolean removed = u.getWatchedMovies().remove(m);
        if (!removed) {
            u.getWatchedMovies().add(m);
        }
        return !removed;
    }
}
