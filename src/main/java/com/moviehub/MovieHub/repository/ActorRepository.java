


package com.moviehub.MovieHub.repository;

import com.moviehub.MovieHub.domain.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    // Aynı isimde birden fazla kayıt olsa bile ilkini döndürür
    Optional<Actor> findFirstByNameIgnoreCase(String name);
}


