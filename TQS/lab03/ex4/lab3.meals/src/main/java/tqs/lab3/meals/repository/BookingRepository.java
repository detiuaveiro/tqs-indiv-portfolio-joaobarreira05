package tqs.lab3.meals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.lab3.meals.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    // Spring automaticamente cria os métodos:
    // save(), findById(), findAll(), deleteById(), etc.
}
