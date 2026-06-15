package meow.at.bms.service;

import meow.at.bms.dto.TheatreDto;
import meow.at.bms.entity.Theatre;
import meow.at.bms.exception.ResourceNotFoundException;
import meow.at.bms.repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TheatreService {

    @Autowired
    private TheatreRepository theatreRepository;

    public TheatreDto createTheater(TheatreDto theatreDto) {
        Theatre theatre = mapToEntity(theatreDto);
        Theatre savedTheatre = theatreRepository.save(theatre);
        return maptoTheatreDto(savedTheatre);
    }

    public TheatreDto getTheatreById(Long id) {
        Theatre theatre  = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id " + id));
        return maptoTheatreDto(theatre);
    }

    public List<TheatreDto> getAllTheatres() {
        List<Theatre> theatreList = theatreRepository.findAll();
        return theatreList.stream()
                .map(this::maptoTheatreDto)
                .collect(Collectors.toList());
    }

    public List<TheatreDto> getTheatreByCity(String city) {
        List<Theatre> theatreList = theatreRepository.findByCity(city);
        return theatreList.stream()
                .map(this::maptoTheatreDto)
                .collect(Collectors.toList());
    }

    public TheatreDto updateTheatre(Long id, TheatreDto theatreDto) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id " + id));

        theatre.setId(theatreDto.getId());
        theatre.setName(theatreDto.getName());
        theatre.setAddress(theatre.getAddress());
        theatre.setCity(theatreDto.getCity());
        theatre.setTotalScreen(theatreDto.getTotalScreens());

        Theatre updatedTheatre = theatreRepository.save(theatre);
        return maptoTheatreDto(updatedTheatre);
    }

    public void deleteTheatre(Long id) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + id));
        theatreRepository.delete(theatre);
    }

    public TheatreDto maptoTheatreDto(Theatre theatre) {
        TheatreDto theatreDto = new TheatreDto();

        theatreDto.setId(theatre.getId());
        theatreDto.setName(theatre.getName());
        theatreDto.setAddress(theatre.getAddress());
        theatreDto.setCity(theatre.getCity());
        theatreDto.setTotalScreens(theatre.getTotalScreen());
        return theatreDto;
    }

    public Theatre mapToEntity(TheatreDto theatreDto) {
        Theatre theatre = new Theatre();

        theatre.setId(theatreDto.getId());
        theatre.setName(theatreDto.getName());
        theatre.setAddress(theatreDto.getAddress());
        theatre.setCity(theatreDto.getCity());
        theatre.setTotalScreen(theatreDto.getTotalScreens());
        return theatre;
    }
}
