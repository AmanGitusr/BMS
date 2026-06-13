package meow.at.bms.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long id;
    private String bookingNumber;
    private LocalDateTime bookingTime;
    private UserDto userDto;
    private ShowDto showDto;
    private String status;
    private double totalAmount;
    private List<ShowSeatDto> showSeatDtos;
    private PaymentDto paymentDto;
}
