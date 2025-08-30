package com.moviehub.MovieHub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank(message = "Kullanıcı adı boş olamaz!")
    @Size(min = 2, max = 50)
    @Column(nullable = false, length = 50)
    private String displayName;

    @Email(message = "Geçerli bir email giriniz")
    @NotBlank(message = "Email boş olamaz!")
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank(message = "Şifre boş olamaz!")
    @Size(min = 6, max = 100, message = "Şifre en az 6 karakter olmalı")
    private String password;

    // ✅ Kullanıcının favori filmleri
    @ManyToMany
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private Set<Movie> favoriteMovies = new HashSet<>();
}
