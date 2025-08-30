package com.moviehub.MovieHub.Dto;

import com.moviehub.MovieHub.domain.Movie;
import com.moviehub.MovieHub.domain.User;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String displayName;
    private String email;
    // İstemezsen kaldırabilirsin: sadece film isimlerini döndürüyoruz
    private Set<String> favoriteMovies;

    // Entity -> DTO
    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .favoriteMovies(
                        user.getFavoriteMovies()
                                .stream()
                                .map(Movie::getMovieName)
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
