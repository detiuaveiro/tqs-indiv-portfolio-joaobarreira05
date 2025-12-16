package tqs.lab3.meals.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.lab3.meals.entity.Booking;
import tqs.lab3.meals.service.BookingService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    // POST /api/bookings - Criar nova reserva
    @PostMapping
    public ResponseEntity<String> createBooking(@RequestBody Map<String, String> request) {
        try {
            String studentId = request.get("studentId");
            String serviceShift = request.get("serviceShift");
            String dietaryRequirements = request.get("dietaryRequirements");
            
            String token = bookingService.bookMeal(studentId, serviceShift, dietaryRequirements);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // GET /api/bookings/{token} - Ver detalhes da reserva
    @GetMapping("/{token}")
    public ResponseEntity<Booking> getBooking(@PathVariable String token) {
        try {
            Booking booking = bookingService.getBookingDetails(token);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET /api/bookings - Ver todas as reservas
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
    
    // PUT /api/bookings/{token}/cancel - Cancelar reserva
    @PutMapping("/{token}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable String token) {
        try {
            boolean cancelled = bookingService.cancelBooking(token);
            if (cancelled) {
                return ResponseEntity.ok("Booking cancelled successfully");
            } else {
                return ResponseEntity.badRequest().body("Cannot cancel booking");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // PUT /api/bookings/{token}/checkin - Fazer check-in
    @PutMapping("/{token}/checkin")
    public ResponseEntity<String> checkIn(@PathVariable String token) {
        try {
            boolean checkedIn = bookingService.checkIn(token);
            if (checkedIn) {
                return ResponseEntity.ok("Check-in successful");
            } else {
                return ResponseEntity.badRequest().body("Cannot check-in");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
