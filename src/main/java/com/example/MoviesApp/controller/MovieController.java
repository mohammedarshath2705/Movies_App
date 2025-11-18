package com.example.MoviesApp.controller;

import com.example.MoviesApp.entity.Movie;
import com.example.MoviesApp.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
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

    @GetMapping("/fetch-today-releases")
    public ResponseEntity<?> fetchTodayReleases() {
        try {
            List<Movie> movies = movieService.fetchAndStoreTodayReleases();

            if (movies.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No movies released today");
            }

            return ResponseEntity.ok(movies);

        } catch (Exception e) {
            e.printStackTrace(); // for debugging/logging

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching today's releases");
        }
    }


    @GetMapping("/moviesByDate")
    public ResponseEntity<?> getMoviesByDate(@RequestParam String date) {
        try {
            LocalDate.parse(date);

            List<Movie> movies = movieService.getMoviesByDate(date);

            if (movies.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No movies found for the selected date.");
            }

            return ResponseEntity.ok(movies);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format. Use 'YYYY-MM-DD'.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving movies for the selected date.");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Movie> getMoviesByTitle(@RequestParam String title) {
        try {
            Optional<Movie> movieOpt = movieService.getMovieByTitle(title);

            if (movieOpt.isEmpty()) {
                System.out.println("Movie not found for title: " + title);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(movieOpt.get());

        } catch (Exception e) {
            e.printStackTrace();  // This will now print the actual cause in logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/all")
    public ResponseEntity<List<Movie>> getMoviesByAllTitle(@RequestParam String title) {
        try {
            List<Movie> movies = movieService.getMoviesAllByTitle(title);

            if (movies.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(movies);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}




