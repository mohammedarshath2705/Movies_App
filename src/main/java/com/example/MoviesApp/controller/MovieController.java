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

    @GetMapping("/fetch/{totalPages}")
    public List<Movie> fetchMoviesFromTMDb(@PathVariable int totalPages) {
        return movieService.fetchAndStoreMovies(totalPages);
    }

    @PutMapping("/updateWithOmdb")
    public ResponseEntity<String> updateMoviesWithOmdbData() {
        movieService.updateMoviesWithOmdbData();
        return ResponseEntity.status(HttpStatus.OK).body("Movies updated !");
    }

    @GetMapping("/byImdbRating")
    public Page<Movie> getMoviesByRating(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return movieService.getMoviesSortedByImdbRating(page, size);
    }

    @GetMapping("/byReleaseDate")
    public Page<Movie> getMoviesSortedByReleaseDate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return movieService.getMoviesSortedByReleaseDate(page, size);
    }

    @GetMapping("/{id}")
    public Movie getMoviesById(@PathVariable UUID id){
        Movie movie = this.movieService.getMovieById(id);
        return new ResponseEntity<>(movie,HttpStatus.CREATED).getBody();
    }

}
