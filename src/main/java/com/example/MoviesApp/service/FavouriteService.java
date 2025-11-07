package com.example.MoviesApp.service;

import com.example.MoviesApp.entity.Movie;
import com.example.MoviesApp.entity.User;
import com.example.MoviesApp.repository.MovieRepository;
import com.example.MoviesApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavouriteService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;


    @Transactional
    @CacheEvict(value = "favourites", key = "#userId")  // Clear cache for that user
    public int addFavourite(UUID userId, UUID movieId) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return 1; // USER NOT FOUND

        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) return 2; // MOVIE NOT FOUND

        boolean already = user.getFavorites().stream()
                .anyMatch(m -> m.getId().equals(movieId));
        if (already) return 3; // ALREADY FAVOURITED

        user.getFavorites().add(movie);
        userRepository.save(user);
        return 0; // SUCCESS
    }


    @Transactional
    @CacheEvict(value = "favourites", key = "#userId") // Clear cache when removing too
    public int removeFavourite(UUID userId, UUID movieId) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return 1; // USER NOT FOUND

        boolean removed = user.getFavorites().removeIf(m -> m.getId().equals(movieId));
        if (!removed) return 2; // MOVIE NOT IN FAVOURITES

        userRepository.save(user);
        return 0; // SUCCESS
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "favourites", key = "#userId")
    public List<Movie> getFavourites(UUID userId) {
        System.out.println("Fetching favourites from DB (not cache) for user: " + userId);

        User user = userRepository.findById(userId).orElse(null);
        return user != null ? user.getFavorites() : null;
    }
}
