package meow.at.bms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowDto {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private MovieDto movieDto;
    private ScreenDto screenDto;
    private List<ShowSeatDto> availableSeats;
}
