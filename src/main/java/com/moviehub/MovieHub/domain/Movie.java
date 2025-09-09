package com.moviehub.MovieHub.domain;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.HashSet; // zaten var ama dursun




@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank(message="Film Adı Boş Olamaz!")
    @Size(min= 2, max= 50 , message = "Film adı '${validatedValue}' {min} ve {max} aralığında olmalı!")
    @Column(nullable = false, length = 50)
    private String movieName;

    @NotBlank(message="Film Açıklaması Boş Olamaz!")
    @Size(min= 2, max= 300 , message = "Film açıklaması '${validatedValue}' {min} ve {max} aralığında olmalı!")
    @Column(nullable = false, length = 300)
    private String description;
    //4 karakter ayarı json format araştır
    @NotBlank(message = "Film yılı boş olamaz!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy")
    private String releaseYear;


    private Double imdbRating;

    @NotBlank(message="Film yapımcısı Boş Olamaz!")
    @Size(min= 2, max= 25 , message = "Film yapımcısı '${validatedValue}' {min} ve {max} aralığında olmalı!")
    @Column(nullable = false, length = 25)
    private String producer;

    @NotBlank(message="Film türü Boş Olamaz!")
    @Size(min= 2, max= 30 , message = "Film türü '${validatedValue}' {min} ve {max} aralığında olmalı!")
    @Column(nullable = false, length = 30)
    private String genre;

    @Size(max=300, message="Poster URL'i çok uzun")
    private String posterUrl;

    @Size(max=300, message="Backdrop URL'i çok uzun")
    private String backdropUrl;

    @Size(max=300, message="Trailer URL'i çok uzun")
    private String trailerUrl;

    // Gallery için JSON olarak saklanacak
    @Column(columnDefinition = "TEXT")
    private String gallery; // JSON string olarak saklanacak

    @ManyToMany
    @JoinTable(
            name = "movie_cast",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Actor> cast = new HashSet<>();
}