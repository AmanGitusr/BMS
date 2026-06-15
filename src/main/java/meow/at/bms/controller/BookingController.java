package meow.at.bms.controller;

import jakarta.validation.Valid;
import meow.at.bms.dto.BookingDto;
import meow.at.bms.dto.BookingRequestDto;
import meow.at.bms.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return new ResponseEntity<>(bookingService.createBooking(bookingRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

}
