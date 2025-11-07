package com.example.MoviesApp.controller;

import com.example.MoviesApp.entity.Movie;
import com.example.MoviesApp.entity.User;
import com.example.MoviesApp.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/favourites")
@RequiredArgsConstructor
public class FavouriteController {

    private final FavouriteService favouriteService;

    @PostMapping("/add")
    public ResponseEntity<Integer> addFavourite(
            @RequestParam UUID userId,
            @RequestParam UUID movieId) {

        int result = favouriteService.addFavourite(userId, movieId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Integer> removeFavourite(
            @RequestParam UUID userId,
            @RequestParam UUID movieId) {

        int result = favouriteService.removeFavourite(userId, movieId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getFavourites(@RequestParam UUID userId) {

        List<Movie> favourites = favouriteService.getFavourites(userId);

        if (favourites == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        return ResponseEntity.ok(favourites);
    }
}
