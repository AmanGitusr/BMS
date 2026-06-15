package meow.at.bms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingNumber;

    @Column(nullable = false)
    private LocalDateTime bookingTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(nullable = false)
    private String status; // conform, cancel, penning

    @Column(nullable = false)
    private Double totalAmount;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<ShowSeat> showSeats;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;

}
