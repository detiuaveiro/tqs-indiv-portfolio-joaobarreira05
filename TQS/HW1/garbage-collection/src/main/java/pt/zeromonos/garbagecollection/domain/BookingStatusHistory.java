package pt.zeromonos.garbagecollection.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_status_history")
@Getter
@Setter
@NoArgsConstructor
public class BookingStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private BookingRequest booking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @Column(length = 255)
    private String note;

    public BookingStatusHistory(BookingRequest booking, BookingStatus status, LocalDateTime changedAt, String note) {
        this.booking = booking;
        this.status = status;
        this.changedAt = changedAt;
        this.note = note;
    }
}
