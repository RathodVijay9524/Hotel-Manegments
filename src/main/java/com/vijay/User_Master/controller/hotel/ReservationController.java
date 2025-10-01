package com.vijay.User_Master.controller.hotel;

import com.vijay.User_Master.dto.hotel.CreateReservationRequest;
import com.vijay.User_Master.dto.hotel.TableReservationDTO;
import com.vijay.User_Master.entity.hotel.RestaurantTable;
import com.vijay.User_Master.service.hotel.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotel/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservationController {
    
    private final ReservationService reservationService;
    
    @PostMapping
    public ResponseEntity<TableReservationDTO> createReservation(@RequestBody CreateReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(request));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TableReservationDTO> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }
    
    @GetMapping("/number/{reservationNumber}")
    public ResponseEntity<TableReservationDTO> getReservationByNumber(@PathVariable String reservationNumber) {
        return ResponseEntity.ok(reservationService.getReservationByNumber(reservationNumber));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TableReservationDTO>> getReservationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<TableReservationDTO>> getUpcomingReservations() {
        return ResponseEntity.ok(reservationService.getUpcomingReservations());
    }
    
    @GetMapping("/date/{date}")
    public ResponseEntity<List<TableReservationDTO>> getReservationsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(reservationService.getReservationsByDate(date));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TableReservationDTO>> getReservationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(reservationService.getReservationsByStatus(status));
    }
    
    @PostMapping("/{id}/confirm")
    public ResponseEntity<TableReservationDTO> confirmReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }
    
    @PostMapping("/{id}/check-in")
    public ResponseEntity<TableReservationDTO> checkInReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.checkInReservation(id));
    }
    
    @PostMapping("/{id}/check-out")
    public ResponseEntity<TableReservationDTO> checkOutReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.checkOutReservation(id));
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<TableReservationDTO> cancelReservation(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String reason = request.getOrDefault("reason", "Cancelled by customer");
        return ResponseEntity.ok(reservationService.cancelReservation(id, reason));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<TableReservationDTO> updateReservationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, request.get("status")));
    }
    
    @GetMapping("/tables/available")
    public ResponseEntity<List<RestaurantTable>> getAvailableTables(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime reservationTime,
            @RequestParam Integer numberOfGuests,
            @RequestParam(defaultValue = "120") Integer durationMinutes) {
        return ResponseEntity.ok(reservationService.getAvailableTables(reservationTime, numberOfGuests, durationMinutes));
    }
    
    @GetMapping("/tables/{tableId}/check-availability")
    public ResponseEntity<Map<String, Boolean>> checkTableAvailability(
            @PathVariable Long tableId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime reservationTime,
            @RequestParam(defaultValue = "120") Integer durationMinutes) {
        boolean isAvailable = reservationService.isTableAvailable(tableId, reservationTime, durationMinutes);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }
}
