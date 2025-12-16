package tqs.lab3.meals.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tqs.lab3.meals.entity.Booking;
import tqs.lab3.meals.entity.BookingStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository test using @DataJpaTest for sliced context.
 * Only JPA components are loaded, using H2 in-memory database.
 * (Test type A from the Employee example)
 */
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void whenSaveBooking_thenFindById() {
        Booking booking = new Booking("tok1", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        entityManager.persistAndFlush(booking);

        Optional<Booking> found = bookingRepository.findById("tok1");
        
        assertThat(found).isPresent();
        assertThat(found.get().getStudentId()).isEqualTo("stu1");
        assertThat(found.get().getServiceShift()).isEqualTo("LUNCH");
    }

    @Test
    void whenInvalidToken_thenReturnEmpty() {
        Optional<Booking> notFound = bookingRepository.findById("invalid-token");
        
        assertThat(notFound).isEmpty();
    }

    @Test
    void whenFindByStatus_thenReturnMatchingBookings() {
        Booking active1 = new Booking("tok1", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        Booking active2 = new Booking("tok2", "stu2", "DINNER", BookingStatus.ACTIVE, "vegan");
        Booking used = new Booking("tok3", "stu3", "LUNCH", BookingStatus.USED, "none");
        Booking cancelled = new Booking("tok4", "stu4", "DINNER", BookingStatus.CANCELLED, "none");

        entityManager.persistAndFlush(active1);
        entityManager.persistAndFlush(active2);
        entityManager.persistAndFlush(used);
        entityManager.persistAndFlush(cancelled);

        List<Booking> activeBookings = bookingRepository.findByStatus(BookingStatus.ACTIVE);
        List<Booking> usedBookings = bookingRepository.findByStatus(BookingStatus.USED);
        List<Booking> cancelledBookings = bookingRepository.findByStatus(BookingStatus.CANCELLED);

        assertThat(activeBookings).hasSize(2);
        assertThat(usedBookings).hasSize(1);
        assertThat(cancelledBookings).hasSize(1);
    }

    @Test
    void whenFindByStudentId_thenReturnStudentBookings() {
        Booking b1 = new Booking("tok1", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        Booking b2 = new Booking("tok2", "stu1", "DINNER", BookingStatus.USED, "none");
        Booking b3 = new Booking("tok3", "stu2", "LUNCH", BookingStatus.ACTIVE, "vegan");

        entityManager.persistAndFlush(b1);
        entityManager.persistAndFlush(b2);
        entityManager.persistAndFlush(b3);

        List<Booking> stu1Bookings = bookingRepository.findByStudentId("stu1");
        List<Booking> stu2Bookings = bookingRepository.findByStudentId("stu2");
        List<Booking> unknownBookings = bookingRepository.findByStudentId("unknown");

        assertThat(stu1Bookings).hasSize(2);
        assertThat(stu2Bookings).hasSize(1);
        assertThat(unknownBookings).isEmpty();
    }

    @Test
    void whenFindByServiceShift_thenReturnShiftBookings() {
        Booking b1 = new Booking("tok1", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        Booking b2 = new Booking("tok2", "stu2", "LUNCH", BookingStatus.ACTIVE, "vegan");
        Booking b3 = new Booking("tok3", "stu3", "DINNER", BookingStatus.ACTIVE, "none");

        entityManager.persistAndFlush(b1);
        entityManager.persistAndFlush(b2);
        entityManager.persistAndFlush(b3);

        List<Booking> lunchBookings = bookingRepository.findByServiceShift("LUNCH");
        List<Booking> dinnerBookings = bookingRepository.findByServiceShift("DINNER");

        assertThat(lunchBookings).hasSize(2);
        assertThat(dinnerBookings).hasSize(1);
    }

    @Test
    void whenFindAll_thenReturnAllBookings() {
        Booking b1 = new Booking("tok1", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        Booking b2 = new Booking("tok2", "stu2", "DINNER", BookingStatus.USED, "vegan");

        entityManager.persistAndFlush(b1);
        entityManager.persistAndFlush(b2);

        List<Booking> allBookings = bookingRepository.findAll();

        assertThat(allBookings).hasSize(2);
    }

    @Test
    void whenDeleteBooking_thenRemoveFromDb() {
        Booking booking = new Booking("tok1", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        entityManager.persistAndFlush(booking);

        bookingRepository.deleteById("tok1");
        entityManager.flush();

        Optional<Booking> deleted = bookingRepository.findById("tok1");
        assertThat(deleted).isEmpty();
    }
}
