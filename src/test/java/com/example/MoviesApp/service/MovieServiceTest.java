package com.example.MoviesApp.service;

import com.example.MoviesApp.entity.Movie;
import com.example.MoviesApp.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    public MovieServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMovies_ReturnsSortedPagedMovies() {
        Movie movie1 = new Movie();
        movie1.setId(UUID.randomUUID());
        movie1.setTitle("A Movie");

        Movie movie2 = new Movie();
        movie2.setId(UUID.randomUUID());
        movie2.setTitle("B Movie");

        List<Movie> movies = List.of(movie1, movie2);
        Page<Movie> expectedPage = new PageImpl<>(movies);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        when(movieRepository.findAll(pageRequest)).thenReturn(expectedPage);

        Page<Movie> result = movieService.getAllMovies(0, 10);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("A Movie", result.getContent().get(0).getTitle());

        verify(movieRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void testGetAllMovies_EmptyResult() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Page<Movie> emptyPage = new PageImpl<>(List.of());
        when(movieRepository.findAll(pageRequest)).thenReturn(emptyPage);

        Page<Movie> result = movieService.getAllMovies(0, 10);

        assertTrue(result.isEmpty());
        verify(movieRepository, times(1)).findAll(pageRequest);
    }
}
