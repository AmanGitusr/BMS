package meow.at.bms.service;

import jakarta.transaction.Transactional;
import meow.at.bms.dto.*;
import meow.at.bms.entity.*;
import meow.at.bms.exception.ResourceNotFoundException;
import meow.at.bms.exception.SeatUnavailableException;
import meow.at.bms.repository.BookingRepository;
import meow.at.bms.repository.ShowRepository;
import meow.at.bms.repository.ShowSeatRepository;
import meow.at.bms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional
    public BookingDto createBooking(BookingRequestDto bookingRequestDto) {
        User user = userRepository
                .findById(bookingRequestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        Show show = showRepository.findById(bookingRequestDto.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found!"));

        List<ShowSeat> selectedSeatList = showSeatRepository.findAllById(bookingRequestDto.getSeatIds());

        for (ShowSeat seat : selectedSeatList) {
            if (!"AVAILABLE".equals(seat.getStatus())) {
                throw new SeatUnavailableException("Seat "
                + seat.getSeat().getSeatNumber()
                + " is not available.");
            }

            seat.setStatus("LOCKED");
        }

        showSeatRepository.saveAll(selectedSeatList);

        Double totalAmount = selectedSeatList
                .stream()
                .mapToDouble(ShowSeat::getPrice)
                .sum();

        Payment payment = new Payment();
        payment.setAmount(totalAmount);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentMethod(bookingRequestDto.getPaymentMethod());
        payment.setStatus("SUCCESS");
        payment.setTransactionId(UUID.randomUUID().toString());


        // booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus("CONFIRMED");
        booking.setTotalAmount(totalAmount);
        booking.setBookingNumber(UUID.randomUUID().toString());
        booking.setPayment(payment);

        Booking saveBooking = bookingRepository.save(booking);

        selectedSeatList.forEach(showSeat ->
                {
                    showSeat.setStatus("BOOKED");
                    showSeat.setBooking(saveBooking);
                });

        showSeatRepository.saveAll(selectedSeatList);

        return mapToBookingDto(saveBooking, selectedSeatList);

    }

    public BookingDto getBookingById(Long id) {
        Booking booking = bookingRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found!"));

        List<ShowSeat> showSeatList = showSeatRepository.findAll()
                .stream()
                .filter(showSeat ->
                    showSeat.getBooking() != null && showSeat.getBooking()
                            .getId()
                            .equals(booking.getId()))
                .collect(Collectors.toList());
        return mapToBookingDto(booking, showSeatList);
    }

    private BookingDto getBookingByNumber(String bookingNumber) {
        Booking booking = bookingRepository
                .findByBookingNumber(bookingNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking number is not found!"));

        List<ShowSeat> showSeatList = showSeatRepository.findAll()
                .stream()
                .filter(showSeat ->
                        showSeat.getBooking() != null && showSeat.getBooking()
                                .getId()
                                .equals(booking.getId()))
                .collect(Collectors.toList());
        return mapToBookingDto(booking, showSeatList);
    }

    private List<BookingDto> getBookingByUserId(Long userid) {
        List<Booking> bookingList = bookingRepository.findByUserId(userid);

        return bookingList.stream()
                .map(booking -> {
                    List<ShowSeat> showSeatList = showSeatRepository.findAll()
                            .stream()
                            .filter(showSeat -> showSeat.getBooking() != null && showSeat.getBooking().getId().equals(booking.getId()))
                            .collect(Collectors.toList());

                    return mapToBookingDto(booking, showSeatList);
                }).collect(Collectors.toList());

    }

    @Transactional
    public BookingDto cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found!"));

        booking.setStatus("CANCELLED");

        List<ShowSeat> showSeatList = showSeatRepository.findAll()
                .stream()
                .filter(showSeat ->
                        showSeat.getBooking() != null && showSeat.getBooking()
                                .getId()
                                .equals(booking.getId()))
                .collect(Collectors.toList());

        showSeatList.forEach(showSeat -> {
            showSeat.setStatus("AVAILABLE");
            showSeat.setBooking(null);
        });

        if (booking.getPayment() != null) {
            booking.getPayment().setStatus("REFUNDED");
        }

        Booking updateBooking = bookingRepository.save(booking);
        showSeatRepository.saveAll(showSeatList);
        return mapToBookingDto(updateBooking, showSeatList);
    }

    private BookingDto mapToBookingDto(Booking booking, List<ShowSeat> seatList) {

        // booking mapping
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId((booking.getId()));
        bookingDto.setBookingNumber(booking.getBookingNumber());
        bookingDto.setBookingTime(booking.getBookingTime());
        bookingDto.setStatus(bookingDto.getStatus());
        bookingDto.setTotalAmount(booking.getTotalAmount());

        // user mapping
        UserDto userDto = new UserDto();
        userDto.setId(booking.getUser().getId());
        userDto.setName(booking.getUser().getName());
        userDto.setEmail(booking.getUser().getEmail());
        userDto.setPhoneNumber(booking.getUser().getPhoneNumber());
        bookingDto.setUserDto(userDto);

        // show mapping
        ShowDto showDto = new ShowDto();
        showDto.setId(booking.getShow().getId());
        showDto.setStartTime(booking.getShow().getStartTime());
        showDto.setEndTime(booking.getShow().getEndTime());

        // movie mapping
        MovieDto movieDto = new MovieDto();
        movieDto.setId(booking.getShow().getMovie().getId());
        movieDto.setTitle(booking.getShow().getMovie().getTitle());
        movieDto.setDescription(booking.getShow().getMovie().getDescription());
        movieDto.setLanguage(booking.getShow().getMovie().getLanguage());
        movieDto.setGenre(booking.getShow().getMovie().getGenre());
        movieDto.setDurationMins(booking.getShow().getMovie().getDurationMins());
        movieDto.setReleaseDate(booking.getShow().getMovie().getReleaseDate());
        movieDto.setPosterUrl(booking.getShow().getMovie().getPosterUrl());
        showDto.setMovieDto(movieDto);

        // Screen mapping
        ScreenDto screenDto = new ScreenDto();
        screenDto.setId(booking.getShow().getScreen().getId());
        screenDto.setName(booking.getShow().getScreen().getName());
        screenDto.setTotalSeats(booking.getShow().getScreen().getTotalSeats());

        TheatreDto theatreDto = new TheatreDto();
        theatreDto.setId(booking.getShow().getScreen().getTheatre().getId());
        theatreDto.setName(booking.getShow().getScreen().getTheatre().getName());
        theatreDto.setAddress(booking.getShow().getScreen().getTheatre().getAddress());
        theatreDto.setCity(booking.getShow().getScreen().getTheatre().getCity());
        theatreDto.setTotalScreens(booking.getShow().getScreen().getTheatre().getTotalScreen());

        screenDto.setTheatreDto(theatreDto);
        showDto.setScreenDto(screenDto);
        bookingDto.setShowDto(showDto);

        List<ShowSeatDto> showSeatDtosList = seatList.stream()
                .map(showSeatList -> {

                    ShowSeatDto showSeatDto = new ShowSeatDto();
                    showSeatDto.setId(showSeatList.getId());
                    showSeatDto.setStatus(showSeatList.getStatus());
                    showSeatDto.setPrice(showSeatList.getPrice());

                    SeatDto baseSeatDto = new SeatDto();
                    baseSeatDto.setId(showSeatList.getSeat().getId());
                    baseSeatDto.setSeatNumber(showSeatList.getSeat().getSeatNumber());
                    baseSeatDto.setSeatType(showSeatList.getSeat().getSeatType());
                    baseSeatDto.setBasePrice(showSeatList.getSeat().getBasePrice());
                    showSeatDto.setSeatDto(baseSeatDto);

                    return showSeatDto;

                })
                .collect(Collectors.toList());

        bookingDto.setShowSeatDtos(showSeatDtosList);

        if(booking.getPayment() != null) {
            PaymentDto paymentDto = new PaymentDto();
            paymentDto.setId(booking.getPayment().getId());
            paymentDto.setAmount(booking.getPayment().getAmount());
            paymentDto.setPaymentMethod(booking.getPayment().getPaymentMethod());
            paymentDto.setPaymentTime(booking.getPayment().getPaymentTime());
            paymentDto.setStatus(booking.getPayment().getStatus());
            paymentDto.setTransactionId(booking.getPayment().getTransactionId());
            bookingDto.setPaymentDto((paymentDto));
        }

        return bookingDto;
    }
}
