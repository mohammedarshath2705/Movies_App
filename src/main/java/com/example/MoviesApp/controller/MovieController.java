package com.example.MoviesApp.controller;

import com.example.MoviesApp.entity.Movie;
import com.example.MoviesApp.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }


    @GetMapping("/fetch")
    public ResponseEntity<List<Movie>> fetchMoviesFromTMDb(
            @RequestParam(defaultValue = "1") int startPage,
            @RequestParam int totalPages) {
        try {
            List<Movie> movies = movieService.fetchAndStoreMovies(startPage, totalPages);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    @GetMapping("/byImdbRating")
    public ResponseEntity<Page<Movie>> getMoviesByRating(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Movie> movies = movieService.getMoviesSortedByImdbRating(page, size);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/byReleaseDate")
    public ResponseEntity<Page<Movie>> getMoviesSortedByReleaseDate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Movie> movies = movieService.getMoviesSortedByReleaseDate(page, size);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMoviesById(@PathVariable UUID id) {
        try {
            Movie movie = movieService.getMovieById(id);
            if (movie == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(movie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public Page<Movie> getAllMovies(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return movieService.getAllMovies(page, size);
    }


}
