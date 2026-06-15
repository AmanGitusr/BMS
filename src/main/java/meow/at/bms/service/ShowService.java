package meow.at.bms.service;

import meow.at.bms.dto.*;
import meow.at.bms.entity.Movie;
import meow.at.bms.entity.Screen;
import meow.at.bms.entity.Show;
import meow.at.bms.entity.ShowSeat;
import meow.at.bms.exception.ResourceNotFoundException;
import meow.at.bms.repository.MovieRepository;
import meow.at.bms.repository.ScreenRepository;
import meow.at.bms.repository.ShowRepository;
import meow.at.bms.repository.ShowSeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;


    public ShowDto createShow(ShowDto showDto) {

        Show show = new Show();
        Movie movie = movieRepository.findById(showDto.getMovieDto().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found!"));

        Screen screen = screenRepository.findById(showDto.getMovieDto().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found!"));

        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(showDto.getStartTime());
        show.setEndTime(showDto.getEndTime());

        Show savedShow = showRepository.save(show);

        List<ShowSeat> availableSeatList = showSeatRepository
                .findByShowIdAndStatus(
                        savedShow.getId(),
                        "AVAILABLE");

        return mapToShowDto(savedShow, availableSeatList);
    }

    public ShowDto getShowById(Long id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));

        List<ShowSeat> availableSeatList = showSeatRepository
                .findByShowIdAndStatus(
                        show.getId(),
                        "AVAILABLE");
        return mapToShowDto(show, availableSeatList);
    }

    public List<ShowDto> getAllShows() {
        List<Show> showList = showRepository.findAll();
        return showList.stream()
                .map(show -> {
                    List<ShowSeat> availableShowSeatList = showSeatRepository.findByShowIdAndStatus(
                            show.getId(),
                            "AVAILABLE"
                    );
                    return mapToShowDto(show, availableShowSeatList);
                })
                .collect(Collectors.toList());
    }

    public List<ShowDto> getShowsByMovie(Long movieId) {
        List<Show> showList = showRepository.findByMovieId(movieId);
        return showList.stream()
                .map(show -> {
                    List<ShowSeat> availableShowSeatList = showSeatRepository.findByShowIdAndStatus(
                            show.getId(),
                            "AVAILABLE"
                    );
                    return mapToShowDto(show, availableShowSeatList);
                })
                .collect(Collectors.toList());
    }

    public List<ShowDto> getShowsByMovieAndCity(Long movieId, String city) {
        List<Show> showList = showRepository.findByMovie_IdAndScreen_Theatre_City(movieId, city);
        return showList.stream()
                .map(show -> {
                    List<ShowSeat> availableShowSeatList = showSeatRepository.findByShowIdAndStatus(
                            show.getId(),
                            "AVAILABLE"
                    );
                    return mapToShowDto(show, availableShowSeatList);
                })
                .collect(Collectors.toList());
    }

    public List<ShowDto> getShowsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Show> showList = showRepository.findByStartTimeBetween(startDate, endDate);
        return showList.stream()
                .map(show -> {
                    List<ShowSeat> availableShowSeatList = showSeatRepository.findByShowIdAndStatus(
                            show.getId(),
                            "AVAILABLE"
                    );
                    return mapToShowDto(show, availableShowSeatList);
                })
                .collect(Collectors.toList());
    }

    private ShowDto mapToShowDto(Show show, List<ShowSeat> availableSeatList) {

        ShowDto showDto = new ShowDto();
        showDto.setId(show.getId());
        showDto.setStartTime(show.getStartTime());
        showDto.setEndTime(show.getEndTime());

        showDto.setMovieDto(new MovieDto(
                show.getMovie().getId(),
                show.getMovie().getTitle(),
                show.getMovie().getDescription(),
                show.getMovie().getLanguage(),
                show.getMovie().getGenre(),
                show.getMovie().getReleaseDate(),
                show.getMovie().getDurationMins(),
                show.getMovie().getPosterUrl()
        ));

        TheatreDto theatreDto = new TheatreDto(
                show.getScreen().getTheatre().getId(),
                show.getScreen().getTheatre().getName(),
                show.getScreen().getTheatre().getAddress(),
                show.getScreen().getTheatre().getCity(),
                show.getScreen().getTheatre().getTotalScreen()
        );

        showDto.setScreenDto(new ScreenDto(
                show.getScreen().getId(),
                show.getScreen().getName(),
                show.getScreen().getTotalSeats(),
                theatreDto
        ));

        List<ShowSeatDto> showSeatDtoList = availableSeatList.stream()
                .map(showSeat -> {
                    ShowSeatDto showSeatDto = new ShowSeatDto();
                    showSeatDto.setId(showSeat.getId());
                    showSeatDto.setStatus(showSeat.getStatus());
                    showSeatDto.setPrice(showSeat.getPrice());

                    SeatDto baseSeatDto = new SeatDto();
                    baseSeatDto.setId(showSeat.getSeat().getId());
                    baseSeatDto.setSeatNumber(showSeat.getSeat().getSeatNumber());
                    baseSeatDto.setSeatType(showSeat.getSeat().getSeatType());
                    baseSeatDto.setBasePrice(showSeat.getSeat().getBasePrice());

                     showSeatDto.setSeatDto(baseSeatDto);
                    return showSeatDto;
                })
                .collect(Collectors.toList());

        showDto.setAvailableSeats(showSeatDtoList);
        return showDto;
    }
}
