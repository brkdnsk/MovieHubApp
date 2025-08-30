package com.moviehub.MovieHub.controller;

import com.moviehub.MovieHub.domain.Movie;
import com.moviehub.MovieHub.domain.User;
import com.moviehub.MovieHub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5500")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Kullanıcı kayıt
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    // Kullanıcı detay
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUser(id));
    }

    // Tüm kullanıcılar
    @GetMapping
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userService.listAll());
    }

    // Favori ekle
    @PostMapping("/{userId}/favorites/{movieId}")
    public ResponseEntity<Map<String, Object>> addFavorite(@PathVariable Long userId,
                                                           @PathVariable Long movieId) {
        userService.addFavorite(userId, movieId);
        return ResponseEntity.ok(Map.of("message", "Favorilere eklendi", "movieId", movieId));
    }

    // Favori sil
    @DeleteMapping("/{userId}/favorites/{movieId}")
    public ResponseEntity<Map<String, Object>> removeFavorite(@PathVariable Long userId,
                                                              @PathVariable Long movieId) {
        userService.removeFavorite(userId, movieId);
        return ResponseEntity.ok(Map.of("message", "Favoriden çıkarıldı", "movieId", movieId));
    }

    // Favorileri listele
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<Set<Movie>> listFavorites(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.listFavorites(userId));
    }
}
