package com.example.MoviesApp.repository;

import com.example.MoviesApp.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    boolean existsByTitle(String title);
   // Page<Movie> findAllByOrderByImdbRatingDesc(Pageable pageable);
   Page<Movie> findByImdbRatingIsNotNull(Pageable pageable);
    Page<Movie> findAll(Pageable pageable);


    @Override
    Optional<Movie> findById(UUID id);
}
