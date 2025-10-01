package com.vijay.User_Master.dto.hotel.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationAnalyticsDTO {
    // Reservation metrics
    private Long totalReservations;
    private Long upcomingReservations;
    private Long todayReservations;
    private Long completedReservations;
    private Long cancelledReservations;
    private Long noShowReservations;
    
    // Table metrics
    private Long totalTables;
    private Long occupiedTables;
    private Long availableTables;
    private Double occupancyRate;
    
    // Booking patterns
    private Map<Integer, Long> reservationsByHour;
    private Map<String, Long> reservationsByDay;
    
    // Popular tables
    private List<PopularTableDTO> mostBookedTables;
    
    // Special occasions
    private Map<String, Long> reservationsByOccasion;
    
    // Average metrics
    private Double averageGuestsPerReservation;
    private Double averageDurationMinutes;
    private Double cancellationRate;
}
