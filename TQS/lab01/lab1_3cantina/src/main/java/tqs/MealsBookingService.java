package tqs;

import java.util.*;

public class MealsBookingService {
    
    private Map<String, Booking> bookings;
    private Map<String, Set<String>> studentBookingsPerShift;
    private Map<String, Integer> shiftCapacities;
    private Map<String, Integer> currentBookingsPerShift;
    
    public MealsBookingService() {
        this.bookings = new HashMap<>();
        this.studentBookingsPerShift = new HashMap<>();
        this.shiftCapacities = new HashMap<>();
        this.currentBookingsPerShift = new HashMap<>();
        
        // Default capacities
        shiftCapacities.put("LUNCH", 100);
        shiftCapacities.put("DINNER", 80);
    }
    
    public String bookMeal(String studentId, String serviceShift) {
        return bookMeal(studentId, serviceShift, null);
    }
    
    public String bookMeal(String studentId, String serviceShift, String dietaryRequirements) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
        
        if (serviceShift == null || serviceShift.trim().isEmpty()) {
            throw new IllegalArgumentException("Service shift cannot be null or empty");
        }
        
        // Check if student already has booking for this shift
        Set<String> studentsInShift = studentBookingsPerShift.get(serviceShift);
        if (studentsInShift != null && studentsInShift.contains(studentId)) {
            throw new IllegalStateException("Student already has a booking for this shift");
        }
        
        // Check capacity
        if (getAvailableCapacity(serviceShift) <= 0) {
            throw new IllegalStateException("No available capacity for this shift");
        }
        
        String token = generateToken();
        Booking booking = new Booking(token, studentId, serviceShift, BookingStatus.ACTIVE, dietaryRequirements);
        
        bookings.put(token, booking);
        
        studentBookingsPerShift
            .computeIfAbsent(serviceShift, k -> new HashSet<>())
            .add(studentId);
            
        currentBookingsPerShift.merge(serviceShift, 1, Integer::sum);
        
        return token;
    }
    
    public Booking getBookingDetails(String token) {
        Booking booking = bookings.get(token);
        if (booking == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        return booking;
    }
    
    public boolean cancelBooking(String token) {
        Booking booking = bookings.get(token);
        if (booking == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        
        if (booking.getStatus() != BookingStatus.ACTIVE) {
            return false;
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        
        Set<String> studentsInShift = studentBookingsPerShift.get(booking.getServiceShift());
        if (studentsInShift != null) {
            studentsInShift.remove(booking.getStudentId());
        }
        
        currentBookingsPerShift.merge(booking.getServiceShift(), -1, Integer::sum);
        
        return true;
    }
    
    public boolean checkIn(String token) {
        Booking booking = bookings.get(token);
        if (booking == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        
        if (booking.getStatus() != BookingStatus.ACTIVE) {
            return false;
        }
        
        booking.setStatus(BookingStatus.USED);
        return true;
    }
    
    public void setShiftCapacity(String serviceShift, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        shiftCapacities.put(serviceShift, capacity);
    }
    
    public int getAvailableCapacity(String serviceShift) {
        int maxCapacity = shiftCapacities.getOrDefault(serviceShift, 0);
        int currentBookings = currentBookingsPerShift.getOrDefault(serviceShift, 0);
        return maxCapacity - currentBookings;
    }
    
    private String generateToken() {
        return "MEAL-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
}

enum BookingStatus {
    ACTIVE, USED, CANCELLED
}

class Booking {
    private String token;
    private String studentId;
    private String serviceShift;
    private BookingStatus status;
    private String dietaryRequirements;
    
    public Booking(String token, String studentId, String serviceShift, BookingStatus status, String dietaryRequirements) {
        this.token = token;
        this.studentId = studentId;
        this.serviceShift = serviceShift;
        this.status = status;
        this.dietaryRequirements = dietaryRequirements;
    }
    
    public String getToken() { return token; }
    public String getStudentId() { return studentId; }
    public String getServiceShift() { return serviceShift; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public String getDietaryRequirements() { return dietaryRequirements; }
}
