package com.moviehub.MovieHub.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moviehub.MovieHub.domain.Movie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class MovieDto {

    private Long id;

    @NotBlank(message="Film Adı Boş Olamaz!")
    @Size(min= 2, max= 50 , message = "Film adı '${validatedValue}' {min} ve {max} aralığında olmalı!")
    private String movieName;

    @NotBlank(message="Film Açıklaması Boş Olamaz!")
    @Size(min= 2, max= 300 , message = "Film açıklaması '${validatedValue}' {min} ve {max} aralığında olmalı!")
    private String description;
    //4 karakter ayarı json format araştır
    @NotBlank(message = "Film yılı boş olamaz!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy")
    private String releaseYear;


    private Double imdbRating;

    @NotBlank(message="Film yapımcısı Boş Olamaz!")
    @Size(min= 2, max= 25 , message = "Film yapımcısı '${validatedValue}' {min} ve {max} aralığında olmalı!")
    private String producer;

    @NotBlank(message="Film türü Boş Olamaz!")
    @Size(min= 2, max= 30 , message = "Film türü '${validatedValue}' {min} ve {max} aralığında olmalı!")
    private String genre;

    @Size(max=300, message="Poster URL'i çok uzun")
    private String posterUrl;



    public MovieDto(Movie movie) {
        this.id = movie.getId();
        this.movieName = movie.getMovieName();
        this.description = movie.getDescription();
        this.releaseYear = movie.getReleaseYear();
        this.imdbRating = movie.getImdbRating();
        this.producer = movie.getProducer();
        this.genre = movie.getGenre();
        this.posterUrl = movie.getPosterUrl();
    }
}