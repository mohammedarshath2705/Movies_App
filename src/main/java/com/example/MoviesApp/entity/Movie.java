package com.example.MoviesApp.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Entity
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Unique identifier for the movie record in the database

    private String title;
    private String genre;
    private String language;

    @Column(columnDefinition = "text")
    private String overview;

   // private Double rating;
    private String releaseDate;
    private Double imdbRating;

    private String director;

    private String poster;

    @JsonIgnore
    @ManyToMany(mappedBy = "favorites")
    private List<User> usersWhoFavorited = new ArrayList<>();


    //    public Double getRating() {
//        return rating;
//    }
//
//    public void setRating(Double rating) {
//        this.rating = rating;
//    }

}
