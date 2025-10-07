package tqs.lab3.meals.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.lab3.meals.entity.Booking;
import tqs.lab3.meals.entity.BookingStatus;
import tqs.lab3.meals.repository.BookingRepository;

import java.util.*;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    private Map<String, Set<String>> studentBookingsPerShift = new HashMap<>();
    private Map<String, Integer> shiftCapacities = new HashMap<>();
    private Map<String, Integer> currentBookingsPerShift = new HashMap<>();
    
    public BookingService() {
        // Capacidades por defeito
        shiftCapacities.put("LUNCH", 100);
        shiftCapacities.put("DINNER", 80);
    }
    
    public String bookMeal(String studentId, String serviceShift, String dietaryRequirements) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
        
        if (serviceShift == null || serviceShift.trim().isEmpty()) {
            throw new IllegalArgumentException("Service shift cannot be null or empty");
        }
        
        // Verificar se o estudante já tem reserva para este turno
        Set<String> studentsInShift = studentBookingsPerShift.get(serviceShift);
        if (studentsInShift != null && studentsInShift.contains(studentId)) {
            throw new IllegalStateException("Student already has a booking for this shift");
        }
        
        // Verificar capacidade
        if (getAvailableCapacity(serviceShift) <= 0) {
            throw new IllegalStateException("No available capacity for this shift");
        }
        
        String token = generateToken();
        Booking booking = new Booking(token, studentId, serviceShift, BookingStatus.ACTIVE, dietaryRequirements);
        
        // Guardar na base de dados
        bookingRepository.save(booking);
        
        // Atualizar controlo de capacidade
        studentBookingsPerShift
            .computeIfAbsent(serviceShift, k -> new HashSet<>())
            .add(studentId);
            
        currentBookingsPerShift.merge(serviceShift, 1, Integer::sum);
        
        return token;
    }
    
    public Booking getBookingDetails(String token) {
        return bookingRepository.findById(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }
    
    public boolean cancelBooking(String token) {
        Optional<Booking> optionalBooking = bookingRepository.findById(token);
        if (optionalBooking.isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }
        
        Booking booking = optionalBooking.get();
        if (booking.getStatus() != BookingStatus.ACTIVE) {
            return false;
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // Atualizar controlo de capacidade
        Set<String> studentsInShift = studentBookingsPerShift.get(booking.getServiceShift());
        if (studentsInShift != null) {
            studentsInShift.remove(booking.getStudentId());
        }
        
        currentBookingsPerShift.merge(booking.getServiceShift(), -1, Integer::sum);
        
        return true;
    }
    
    public boolean checkIn(String token) {
        Optional<Booking> optionalBooking = bookingRepository.findById(token);
        if (optionalBooking.isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }
        
        Booking booking = optionalBooking.get();
        if (booking.getStatus() != BookingStatus.ACTIVE) {
            return false;
        }
        
        booking.setStatus(BookingStatus.USED);
        bookingRepository.save(booking);
        
        return true;
    }
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    private String generateToken() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    private int getAvailableCapacity(String serviceShift) {
        int totalCapacity = shiftCapacities.getOrDefault(serviceShift, 50);
        int currentBookings = currentBookingsPerShift.getOrDefault(serviceShift, 0);
        return totalCapacity - currentBookings;
    }
}
