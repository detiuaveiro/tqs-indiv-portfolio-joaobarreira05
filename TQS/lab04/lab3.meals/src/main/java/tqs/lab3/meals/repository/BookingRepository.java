package tqs.lab3.meals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tqs.lab3.meals.entity.Booking;
import tqs.lab3.meals.entity.BookingStatus;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    // Spring automatically creates the methods:
    // save(), findById(), findAll(), deleteById(), etc.
    
    // Custom query using @Query annotation (Lab 4 requirement)
    @Query("SELECT b FROM Booking b WHERE b.status = :status")
    List<Booking> findByStatus(@Param("status") BookingStatus status);
    
    // Custom derived query method
    List<Booking> findByStudentId(String studentId);
    
    // Custom query to find bookings by service shift
    @Query("SELECT b FROM Booking b WHERE b.serviceShift = :shift")
    List<Booking> findByServiceShift(@Param("shift") String shift);
}
