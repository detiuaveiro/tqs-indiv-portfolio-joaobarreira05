package tqs.lab3.meals.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tqs.lab3.meals.entity.Booking;
import tqs.lab3.meals.entity.BookingStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void saveAndFind() {
        Booking b = new Booking("r1", "s1", "LUNCH", BookingStatus.ACTIVE, "none");
        bookingRepository.save(b);

        Optional<Booking> found = bookingRepository.findById("r1");
        assertTrue(found.isPresent());
        assertEquals("s1", found.get().getStudentId());
    }
}
