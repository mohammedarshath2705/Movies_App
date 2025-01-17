package com.example.MoviesApp.service;

import com.example.MoviesApp.entity.Movie;
import com.example.MoviesApp.repository.MovieRepository;
import jakarta.validation.constraints.Null;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.base.url}")
    private String baseUrl;

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    @Value("${omdb.base.url}")
    private String omdbBaseUrl;

    @Autowired
    public MovieService(MovieRepository movieRepository, RestTemplate restTemplate) {
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
    }

    public List<Movie> fetchAndStoreMovies(int totalPages) {
        List<Movie> movies = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
            String url = baseUrl + "/movie/top_rated?api_key=" + apiKey + "&page=" + page;
            String response = restTemplate.getForObject(url, String.class);

            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray results = jsonResponse.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject movieJson = results.getJSONObject(i);

                    Movie movie = new Movie();
                    movie.setTitle(movieJson.getString("title"));
                   // movie.setGenre(movieJson.has("genre_ids") ? movieJson.getJSONArray("genre_ids").toString() : "Unknown");
                    //movie.setLanguage(movieJson.getString("original_language"));
                    movie.setOverview(movieJson.getString("overview"));
                    //movie.setRating(movieJson.getDouble("vote_average"));
                    movie.setReleaseDate(movieJson.getString("release_date"));

                    if (!movieRepository.existsByTitle(movie.getTitle())) {
                        movieRepository.save(movie);
                        movies.add(movie);
                    }
                }
            } catch (Exception e) {
                logger.error("Error while fetching movies from TMDb API", e);
            }
        }

        return movies;
    }

    public void updateMoviesWithOmdbData() {
        List<Movie> movies = movieRepository.findAll();

        for (Movie movie : movies) {
            try {
                String url = omdbBaseUrl + "/?apikey=" + omdbApiKey + "&t=" + movie.getTitle();
                String response = restTemplate.getForObject(url, String.class);

                JSONObject omdbResponse = new JSONObject(response);

                if (omdbResponse.has("imdbRating") && !omdbResponse.getString("imdbRating").equals("N/A")) {
                    movie.setImdbRating(Double.parseDouble(omdbResponse.getString("imdbRating")));
                }
                if (omdbResponse.has("Genre")) {
                    movie.setGenre(omdbResponse.getString("Genre"));
                }
                if (omdbResponse.has("Director")) {
                    movie.setDirector(omdbResponse.getString("Director"));
                }
                if(omdbResponse.has("Language")){
                    movie.setLanguage(omdbResponse.getString("Language"));
                }
                if (omdbResponse.has("Poster") && !omdbResponse.getString("Poster").equals("N/A")) {
                    movie.setPoster(omdbResponse.getString("Poster")); // Save poster URL
                }

                movieRepository.save(movie); // Update movie with new data
            } catch (Exception e) {
                logger.error("Error while fetching IMDb data for movie: " + movie.getTitle(), e);
            }
        }
    }

    public Page<Movie> getMoviesSortedByImdbRating(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "imdbRating"));
        return movieRepository.findByImdbRatingIsNotNull(pageRequest);
    }

    public Page<Movie> getMoviesSortedByReleaseDate(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "releaseDate"));
        return movieRepository.findAll(pageRequest);
    }

    public Movie getMovieById(UUID id){
        Optional<Movie> movieOptional = this.movieRepository.findById(id);
        return movieOptional.orElse(null);
    }
}
