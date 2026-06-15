package meow.at.bms.service;

import meow.at.bms.dto.MovieDto;
import meow.at.bms.entity.Movie;
import meow.at.bms.exception.ResourceNotFoundException;
import meow.at.bms.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public MovieDto createMovie(MovieDto movieDto) {
        Movie movie = mapToEntity(movieDto);
        Movie saveMovie = movieRepository.save(movie);
        return maptoMovieDto(saveMovie);
    }

    public MovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return maptoMovieDto(movie);
    }

    public List<MovieDto> getAllMovies() {
        List<Movie> movieList = movieRepository.findAll();
        return movieList.stream()
                .map(this::maptoMovieDto)
                .collect(Collectors.toList());
    }

    public List<MovieDto> getMovieByLanguage(String language) {
        List<Movie> movieList = movieRepository.findByLanguage(language);
        return movieList.stream()
                .map(this::maptoMovieDto)
                .collect(Collectors.toList());
    }

    public List<MovieDto> getMovieByGenre(String genre) {
        List<Movie> movieList = movieRepository.findByGenre(genre);
        return movieList.stream()
                .map(this::maptoMovieDto)
                .collect(Collectors.toList());
    }

    public List<MovieDto> searchMovies(String title) {
        List<Movie> movieList = movieRepository.findByTitleContaining(title);
        return movieList.stream()
                .map(this::maptoMovieDto)
                .collect(Collectors.toList());
    }

    public MovieDto updateMovie(Long id, MovieDto movieDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        movie.setTitle(movieDto.getTitle());
        movie.setDescription(movieDto.getDescription());
        movie.setLanguage(movieDto.getLanguage());
        movie.setGenre(movieDto.getGenre());
        movie.setDurationMins(movieDto.getDurationMins());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setPosterUrl(movieDto.getPosterUrl());

        Movie updatedMovie = movieRepository.save(movie);
        return maptoMovieDto(updatedMovie);
    }

    public void deleteMovie(Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        movieRepository.delete(movie);
    }

    public MovieDto maptoMovieDto(Movie movie) {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(movie.getId());
        movieDto.setTitle(movie.getTitle());
        movieDto.setDescription(movie.getDescription());
        movieDto.setLanguage(movie.getLanguage());
        movieDto.setGenre(movie.getGenre());
        movieDto.setDurationMins(movie.getDurationMins());
        movieDto.setReleaseDate(movie.getReleaseDate());
        movieDto.setPosterUrl(movie.getPosterUrl());

        return movieDto;
    }

    public Movie mapToEntity(MovieDto movieDto) {
        Movie movie = new Movie();
        movie.setTitle(movieDto.getTitle());
        movie.setDescription(movieDto.getDescription());
        movie.setLanguage(movieDto.getLanguage());
        movie.setGenre(movieDto.getGenre());
        movie.setDurationMins(movieDto.getDurationMins());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setPosterUrl(movieDto.getPosterUrl());

        return movie;
    }
}
