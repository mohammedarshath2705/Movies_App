package com.example.MoviesApp.controller;

import com.example.MoviesApp.entity.Movie;
import com.example.MoviesApp.service.MovieService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Test
    void testGetAllMovies_ReturnsPagedMovies() throws Exception {
        Movie movie1 = new Movie();
        movie1.setId(UUID.fromString("724c6361-aba4-4842-bff6-4a17e4dc4d54"));
        movie1.setTitle("10,000 BC");
        movie1.setGenre("Adventure, Action, Drama, Fantasy");
        movie1.setLanguage("English");
        movie1.setOverview("A prehistoric epic that follows a young mammoth hunter...");
        movie1.setReleaseDate("2008-03-04");
        movie1.setImdbRating(5.5);
        movie1.setDirector("Roland Emmerich");
        movie1.setPoster("https://image.tmdb.org/t/p/w780/jdmRey22HOSyfL44PbYYImZUk3L.jpg");

        Movie movie2 = new Movie();
        movie2.setId(UUID.fromString("5ecc5aac-232a-4c6a-89b8-a217d245ce3e"));
        movie2.setTitle("100 Feet");
        movie2.setGenre("Horror");
        movie2.setLanguage("English");
        movie2.setOverview("After Marnie Watson kills her abusive husband...");
        movie2.setReleaseDate("2008-07-22");
        movie2.setImdbRating(5.7);
        movie2.setDirector("Eric Red");
        movie2.setPoster("https://image.tmdb.org/t/p/w780/jZu09TyQAiUaQRHa7L18eVBPzdS.jpg");

        List<Movie> movies = List.of(movie1, movie2);
        Page<Movie> moviePage = new PageImpl<>(movies, PageRequest.of(0, 10), movies.size());

        Mockito.when(movieService.getAllMovies(0, 10)).thenReturn(moviePage);

        mockMvc.perform(get("/movies/all")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("10,000 BC"))
                .andExpect(jsonPath("$.content[1].title").value("100 Feet"))
                .andExpect(jsonPath("$.content[0].genre").value("Adventure, Action, Drama, Fantasy"))
                .andExpect(jsonPath("$.content[1].director").value("Eric Red"));
    }
}
