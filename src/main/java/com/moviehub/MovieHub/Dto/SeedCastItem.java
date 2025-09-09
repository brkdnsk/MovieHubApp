package com.moviehub.MovieHub.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedCastItem {
    @NotBlank
    private String movieName;      // Movie tablosundaki movie_name ile eşleşecek
    @NotEmpty
    private List<String> cast;     // ["Oyuncu 1","Oyuncu 2",...]
}
