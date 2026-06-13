package meow.at.bms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowSeatDto {

    private Long id;
    private SeatDto seatDto;
    private String status;
    private Double price;

}
