package tqs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MealsBookingServiceTest {
    
    private MealsBookingService service;
    
    @BeforeEach
    void setup() {
        service = new MealsBookingService();
    }
    
    @Test
    void bookMealSuccess() {
        String token = service.bookMeal("student1", "LUNCH");
        
        assertNotNull(token);
        assertTrue(token.startsWith("MEAL-"));
        
        Booking booking = service.getBookingDetails(token);
        assertEquals("student1", booking.getStudentId());
        assertEquals("LUNCH", booking.getServiceShift());
        assertEquals(BookingStatus.ACTIVE, booking.getStatus());
    }
    
    @Test
    void noDoubleBooking() {
        service.bookMeal("student1", "LUNCH");
        
        assertThrows(IllegalStateException.class, () -> {
            service.bookMeal("student1", "LUNCH");
        });
    }
    
    @Test
    void capacityLimit() {
        service.setShiftCapacity("LUNCH", 2);
        
        service.bookMeal("student1", "LUNCH");
        service.bookMeal("student2", "LUNCH");
        
        assertThrows(IllegalStateException.class, () -> {
            service.bookMeal("student3", "LUNCH");
        });
    }
    
    @Test
    void checkInBooking() {
        String token = service.bookMeal("student1", "LUNCH");
        
        assertTrue(service.checkIn(token));
        
        Booking booking = service.getBookingDetails(token);
        assertEquals(BookingStatus.USED, booking.getStatus());
    }
    
    @Test
    void usedTicketCannotBeUsedAgain() {
        String token = service.bookMeal("student1", "LUNCH");
        service.checkIn(token);
        
        assertFalse(service.checkIn(token));
    }
    
    @Test
    void cancelledTicketCannotBeUsed() {
        String token = service.bookMeal("student1", "LUNCH");
        service.cancelBooking(token);
        
        assertFalse(service.checkIn(token));
    }
    
    @Test
    void bookMealWithDietaryRequirements() {
        String token = service.bookMeal("student1", "LUNCH", "vegetarian");
        
        Booking booking = service.getBookingDetails(token);
        assertEquals("vegetarian", booking.getDietaryRequirements());
    }
    
    @Test
    void invalidTokenThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.getBookingDetails("invalid-token");
        });
    }
    
    @Test
    void nullStudentThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.bookMeal(null, "LUNCH");
        });
    }
}
