package tqs.lab3.meals.entity;

import jakarta.persistence.*;

@Entity
public class Booking {
    @Id
    private String token;
    
    private String studentId;
    private String serviceShift;
    
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    
    private String dietaryRequirements;
    
    // Construtor vazio (JPA precisa)
    public Booking() {}
    
    // Construtor completo (baseado no Lab 1)
    public Booking(String token, String studentId, String serviceShift, 
                   BookingStatus status, String dietaryRequirements) {
        this.token = token;
        this.studentId = studentId;
        this.serviceShift = serviceShift;
        this.status = status;
        this.dietaryRequirements = dietaryRequirements;
    }
    
    // Getters e setters
    public String getToken() { 
        return token; 
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getStudentId() { 
        return studentId; 
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getServiceShift() { 
        return serviceShift; 
    }
    
    public void setServiceShift(String serviceShift) {
        this.serviceShift = serviceShift;
    }
    
    public BookingStatus getStatus() { 
        return status; 
    }
    
    public void setStatus(BookingStatus status) { 
        this.status = status; 
    }
    
    public String getDietaryRequirements() { 
        return dietaryRequirements; 
    }
    
    public void setDietaryRequirements(String dietaryRequirements) {
        this.dietaryRequirements = dietaryRequirements;
    }
}
