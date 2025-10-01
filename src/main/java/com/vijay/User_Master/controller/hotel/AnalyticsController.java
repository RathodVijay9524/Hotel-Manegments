package com.vijay.User_Master.controller.hotel;

import com.vijay.User_Master.dto.hotel.analytics.*;
import com.vijay.User_Master.service.hotel.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/hotel/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    /**
     * Get overall dashboard overview with key metrics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardOverviewDTO> getDashboardOverview() {
        return ResponseEntity.ok(analyticsService.getDashboardOverview());
    }
    
    /**
     * Get comprehensive order analytics
     * @param startDate Optional start date (default: 7 days ago)
     * @param endDate Optional end date (default: now)
     */
    @GetMapping("/orders")
    public ResponseEntity<OrderAnalyticsDTO> getOrderAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();
        
        return ResponseEntity.ok(analyticsService.getOrderAnalytics(start, end));
    }
    
    /**
     * Get today's order analytics
     */
    @GetMapping("/orders/today")
    public ResponseEntity<OrderAnalyticsDTO> getTodayOrderAnalytics() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return ResponseEntity.ok(analyticsService.getOrderAnalytics(start, end));
    }
    
    /**
     * Get this week's order analytics
     */
    @GetMapping("/orders/this-week")
    public ResponseEntity<OrderAnalyticsDTO> getWeekOrderAnalytics() {
        LocalDateTime start = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return ResponseEntity.ok(analyticsService.getOrderAnalytics(start, end));
    }
    
    /**
     * Get this month's order analytics
     */
    @GetMapping("/orders/this-month")
    public ResponseEntity<OrderAnalyticsDTO> getMonthOrderAnalytics() {
        LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return ResponseEntity.ok(analyticsService.getOrderAnalytics(start, end));
    }
    
    /**
     * Get comprehensive delivery analytics
     * @param startDate Optional start date (default: 7 days ago)
     * @param endDate Optional end date (default: now)
     */
    @GetMapping("/deliveries")
    public ResponseEntity<DeliveryAnalyticsDTO> getDeliveryAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();
        
        return ResponseEntity.ok(analyticsService.getDeliveryAnalytics(start, end));
    }
    
    /**
     * Get today's delivery analytics
     */
    @GetMapping("/deliveries/today")
    public ResponseEntity<DeliveryAnalyticsDTO> getTodayDeliveryAnalytics() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return ResponseEntity.ok(analyticsService.getDeliveryAnalytics(start, end));
    }
    
    /**
     * Get comprehensive reservation analytics
     * @param startDate Optional start date (default: 7 days ago)
     * @param endDate Optional end date (default: now)
     */
    @GetMapping("/reservations")
    public ResponseEntity<ReservationAnalyticsDTO> getReservationAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();
        
        return ResponseEntity.ok(analyticsService.getReservationAnalytics(start, end));
    }
    
    /**
     * Get today's reservation analytics
     */
    @GetMapping("/reservations/today")
    public ResponseEntity<ReservationAnalyticsDTO> getTodayReservationAnalytics() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return ResponseEntity.ok(analyticsService.getReservationAnalytics(start, end));
    }
    
    /**
     * Get this week's reservation analytics
     */
    @GetMapping("/reservations/this-week")
    public ResponseEntity<ReservationAnalyticsDTO> getWeekReservationAnalytics() {
        LocalDateTime start = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return ResponseEntity.ok(analyticsService.getReservationAnalytics(start, end));
    }
    
    /**
     * Get this month's reservation analytics
     */
    @GetMapping("/reservations/this-month")
    public ResponseEntity<ReservationAnalyticsDTO> getMonthReservationAnalytics() {
        LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return ResponseEntity.ok(analyticsService.getReservationAnalytics(start, end));
    }
}
