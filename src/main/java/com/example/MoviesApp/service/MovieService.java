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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.base.url}")
    private String baseUrl;

    @Autowired
    public MovieService(MovieRepository movieRepository, RestTemplate restTemplate) {
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
    }

    private static final Map<String, String> LANGUAGE_MAP = Map.ofEntries(
            Map.entry("en", "English"),
            Map.entry("ja", "Japanese"),
            Map.entry("es", "Spanish"),
            Map.entry("fr", "French"),
            Map.entry("de", "German"),
            Map.entry("zh", "Chinese"),
            Map.entry("hi", "Hindi"),
            Map.entry("ko", "Korean"),
            Map.entry("it", "Italian"),
            Map.entry("pt", "Portuguese"),
            Map.entry("ru", "Russian"),
            Map.entry("ar", "Arabic"),
            Map.entry("tr", "Turkish"),
            Map.entry("nl", "Dutch"),
            Map.entry("sv", "Swedish"),
            Map.entry("pl", "Polish"),
            Map.entry("no", "Norwegian"),
            Map.entry("fi", "Finnish"),
            Map.entry("da", "Danish"),
            Map.entry("th", "Thai"),
            Map.entry("id", "Indonesian"),
            Map.entry("vi", "Vietnamese"),
            Map.entry("he", "Hebrew"),
            Map.entry("uk", "Ukrainian"),
            Map.entry("el", "Greek"),
            Map.entry("cs", "Czech"),
            Map.entry("ro", "Romanian"),
            Map.entry("hu", "Hungarian"),
            Map.entry("bg", "Bulgarian"),
            Map.entry("sr", "Serbian"),
            Map.entry("hr", "Croatian"),
            Map.entry("ms", "Malay"),
            Map.entry("ta", "Tamil"),
            Map.entry("te", "Telugu"),
            Map.entry("ml", "Malayalam"),
            Map.entry("kn", "Kannada"),
            Map.entry("mr", "Marathi"),
            Map.entry("bn", "Bengali"),
            Map.entry("pa", "Punjabi"),
            Map.entry("gu", "Gujarati"),
            Map.entry("am", "Amharic"),
            Map.entry("sw", "Swahili"),
            Map.entry("fa", "Persian"),
            Map.entry("af", "Afrikaans"),
            Map.entry("et", "Estonian"),
            Map.entry("lt", "Lithuanian"),
            Map.entry("lv", "Latvian"),
            Map.entry("sl", "Slovenian"),
            Map.entry("sk", "Slovak"),
            Map.entry("is", "Icelandic")
    );

    private Map<Integer, String> genreMap = new HashMap<>();

    private void fetchGenreMap() {
        if (!genreMap.isEmpty()) {
            return; // Genre map is already populated
        }

        String url = baseUrl + "/genre/movie/list?api_key=" + apiKey + "&language=en-US";
        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray genres = jsonResponse.getJSONArray("genres");

            for (int i = 0; i < genres.length(); i++) {
                JSONObject genre = genres.getJSONObject(i);
                int id = genre.getInt("id");
                String name = genre.getString("name");
                genreMap.put(id, name);
            }
        } catch (Exception e) {
            logger.error("Error fetching genre map from TMDb API", e);
        }
    }

    private String fetchGenres(JSONArray genreIds) {
        fetchGenreMap(); // Ensure the genre map is populated

        if (genreIds == null || genreIds.isEmpty()) {
            return "Unknown";
        }

        List<String> genreNames = new ArrayList<>();
        for (int i = 0; i < genreIds.length(); i++) {
            int genreId = genreIds.getInt(i);
            String genreName = genreMap.getOrDefault(genreId, "Unknown");
            genreNames.add(genreName);
        }
        return String.join(", ", genreNames);
    }

    public List<Movie> fetchAndStoreMovies(int startPage, int totalPages) {
        fetchGenreMap(); // Ensure genre map is populated before processing movies
        List<Movie> movies = new ArrayList<>();
        List<Movie> batchMovies = new ArrayList<>();

        for (int page = startPage; page <= totalPages; page++) {
            String url = baseUrl + "/movie/top_rated?api_key=" + apiKey + "&page=" + page;
            try {
                String response = restTemplate.getForObject(url, String.class);

                JSONObject jsonResponse = new JSONObject(response);
                JSONArray results = jsonResponse.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject movieJson = results.getJSONObject(i);

                    int movieId = movieJson.getInt("id");
                    String title = movieJson.getString("title");
                    Movie movie = movieRepository.findByTitle(title).orElse(new Movie());

                    // Update or set all fields
                    movie.setTitle(title);
                    movie.setOverview(movieJson.optString("overview", "No overview available"));
                    movie.setReleaseDate(movieJson.optString("release_date", "Unknown"));
                    movie.setPoster(buildFullPosterPath(movieJson.optString("poster_path")));
                    movie.setImdbRating(movieJson.optDouble("vote_average", 0.0));
                    movie.setLanguage(getFullLanguageName(movieJson.optString("original_language", "Unknown")));
                    movie.setGenre(fetchGenres(movieJson.optJSONArray("genre_ids")));
                    movie.setDirector(fetchDirector(movieId));

                    // Add to batch
                    batchMovies.add(movie);
                }

                // Save batch when size reaches 50
                if (batchMovies.size() >= 50) {
                    movieRepository.saveAll(batchMovies);
                    movies.addAll(batchMovies);
                    batchMovies.clear(); // Clear the batch
                }

                logger.info("Processed page {}/{}", page, totalPages);

                // Sleep between requests to avoid hitting rate limits
                sleepBetweenRequests();

            } catch (Exception e) {
                logger.error("Error while fetching page " + page + " from TMDb API", e);
                break; // Exit loop on error
            }
        }

        // Save remaining movies in the batch
        if (!batchMovies.isEmpty()) {
            movieRepository.saveAll(batchMovies);
            movies.addAll(batchMovies);
        }

        return movies;
    }

    private String buildFullPosterPath(String posterPath) {
        if (posterPath == null || posterPath.isEmpty()) {
            return null; // Handle cases where posterPath is null
        }
        return "https://image.tmdb.org/t/p/w500" + posterPath; // Change size as needed
    }

    public String fetchDirector(int movieId) {
        String url = baseUrl + "/movie/" + movieId + "/credits?api_key=" + apiKey;
        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray crewArray = jsonResponse.getJSONArray("crew");

            for (int i = 0; i < crewArray.length(); i++) {
                JSONObject crewMember = crewArray.getJSONObject(i);
                if ("Director".equalsIgnoreCase(crewMember.optString("job"))) {
                    return crewMember.optString("name", "Unknown");
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching director information for movie ID: " + movieId, e);
        }
        return "Unknown";
    }

    private String getFullLanguageName(String languageCode) {
        return LANGUAGE_MAP.getOrDefault(languageCode, "Unknown");
    }

    private void sleepBetweenRequests() {
        try {
            Thread.sleep(300); // 300 ms delay (adjust as needed)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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

    public Movie getMovieById(UUID id) {
        Optional<Movie> movieOptional = this.movieRepository.findById(id);
        return movieOptional.orElse(null);
    }

    public Page<Movie> getAllMovies(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "title")); // Sorted by title
        return movieRepository.findAll(pageRequest);
    }
}
