package com.moviehub.MovieHub.controller;

import com.moviehub.MovieHub.Dto.LoginRequest;
import com.moviehub.MovieHub.Dto.UserDto;
import com.moviehub.MovieHub.domain.Movie;
import com.moviehub.MovieHub.domain.User;
import com.moviehub.MovieHub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5500")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Kullanıcı kayıt -> DTO döner (password'ü dışarı sızdırmayız)
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody User user) {
        User saved = userService.register(user);
        return ResponseEntity.ok(UserDto.fromEntity(saved));
    }

    // Kullanıcı detay -> DTO
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(UserDto.fromEntity(userService.findUser(id)));
    }

    // Tüm kullanıcılar -> DTO list
    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        return ResponseEntity.ok(
                userService.listAll().stream()
                        .map(UserDto::fromEntity)
                        .collect(Collectors.toList())
        );
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

    // Favorileri listele (istersen bunu da DTO'ya çevirebiliriz)
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<Set<Movie>> listFavorites(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.listFavorites(userId));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest req) {
        User u = userService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(UserDto.fromEntity(u)); // şifre dışarı çıkmıyor
    }

    // Basit API’de "logout" sunucuda bir şey yapmaz. İstemci tarafı siler.
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        return ResponseEntity.ok(Map.of("message", "Çıkış yapıldı (istemci tarafında token/oturum temizleyin)"));
    }

    // ---------- İZLENENLER (watched) ----------
    /** İzlendi olarak işaretle */
    @PostMapping("/{userId}/watched/{movieId}")
    public ResponseEntity<Map<String, Object>> addWatched(@PathVariable Long userId,
                                                          @PathVariable Long movieId) {
        userService.addWatched(userId, movieId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "İzlendi olarak işaretlendi", "movieId", movieId));
    }

    /** İzlenen işaretini kaldır */
    @DeleteMapping("/{userId}/watched/{movieId}")
    public ResponseEntity<Map<String, Object>> removeWatched(@PathVariable Long userId,
                                                             @PathVariable Long movieId) {
        userService.removeWatched(userId, movieId);
        return ResponseEntity.ok(Map.of("message", "İzlendi işareti kaldırıldı", "movieId", movieId));
    }

    /** Kullanıcının izledikleri listesi */
    @GetMapping("/{userId}/watched")
    public ResponseEntity<Set<Movie>> listWatched(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.listWatched(userId));
    }

    /** Tek film izlenmiş mi? */
    @GetMapping("/{userId}/watched/{movieId}")
    public ResponseEntity<Map<String, Object>> isWatched(@PathVariable Long userId,
                                                         @PathVariable Long movieId) {
        boolean watched = userService.isWatched(userId, movieId);
        return ResponseEntity.ok(Map.of("movieId", movieId, "watched", watched));
    }

    /** Toggle (buton için ideal) */
    @PutMapping("/{userId}/watched/{movieId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleWatched(@PathVariable Long userId,
                                                             @PathVariable Long movieId) {
        boolean nowWatched = userService.toggleWatched(userId, movieId);
        return ResponseEntity.ok(Map.of(
                "movieId", movieId,
                "watched", nowWatched,
                "message", nowWatched ? "İzlendi olarak işaretlendi" : "İzlendi işareti kaldırıldı"
        ));
    }
}
