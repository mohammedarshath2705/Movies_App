package com.example.MoviesApp.repository;

import com.example.MoviesApp.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {


    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) = LOWER(:title)")
    Optional<Movie> findByTitleIgnoreCase(@Param("title") String title);

    // Sort by IMDb rating with pagination
    @Query("SELECT m FROM Movie m ORDER BY m.imdbRating DESC")
    Page<Movie> findAllMoviesSortedByImdbRating(Pageable pageable);

    // Sort by release date with pagination
    @Query("SELECT m FROM Movie m ORDER BY m.releaseDate DESC")
    Page<Movie> findAllMoviesSortedByReleaseDate(Pageable pageable);

    // Find movies by exact release date
    @Query("SELECT m FROM Movie m WHERE m.releaseDate = :releaseDate")
    List<Movie> findAllMoviesByReleaseDate(@Param("releaseDate") String releaseDate);

    // Find movies by release date with pagination
    @Query("SELECT m FROM Movie m WHERE m.releaseDate = :releaseDate ORDER BY m.imdbRating DESC")
    Page<Movie> findMoviesByReleaseDate(@Param("releaseDate") LocalDate releaseDate, Pageable pageable);


}
