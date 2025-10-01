package com.vijay.User_Master.service.hotel;

import com.vijay.User_Master.dto.hotel.CreateReservationRequest;
import com.vijay.User_Master.dto.hotel.TableReservationDTO;
import com.vijay.User_Master.entity.hotel.RestaurantTable;
import com.vijay.User_Master.entity.hotel.TableReservation;
import com.vijay.User_Master.repository.hotel.RestaurantTableRepository;
import com.vijay.User_Master.repository.hotel.TableReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    
    private final TableReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    
    @Transactional
    public TableReservationDTO createReservation(CreateReservationRequest request) {
        log.info("Creating table reservation for user: {}", request.getUserId());
        
        // Validate reservation time
        if (request.getReservationDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot make reservation in the past");
        }
        
        // Get table
        RestaurantTable table = null;
        if (request.getTableId() != null) {
            table = tableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new RuntimeException("Table not found"));
            
            // Check if table is available for this time slot
            if (!isTableAvailable(request.getTableId(), request.getReservationDateTime(), 
                    request.getDurationMinutes() != null ? request.getDurationMinutes() : 120)) {
                throw new RuntimeException("Table is not available for the selected time");
            }
        }
        
        // Generate reservation number
        String reservationNumber = generateReservationNumber();
        
        TableReservation reservation = TableReservation.builder()
                .reservationNumber(reservationNumber)
                .userId(request.getUserId())
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .customerEmail(request.getCustomerEmail())
                .table(table)
                .numberOfGuests(request.getNumberOfGuests())
                .reservationDateTime(request.getReservationDateTime())
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 120)
                .status(TableReservation.ReservationStatus.PENDING)
                .specialRequests(request.getSpecialRequests())
                .occasion(request.getOccasion())
                .requiresHighChair(request.getRequiresHighChair() != null ? request.getRequiresHighChair() : false)
                .requiresWheelchairAccess(request.getRequiresWheelchairAccess() != null ? request.getRequiresWheelchairAccess() : false)
                .build();
        
        reservation = reservationRepository.save(reservation);
        
        log.info("Reservation created: {}", reservationNumber);
        return mapToReservationDTO(reservation);
    }
    
    @Transactional
    public TableReservationDTO updateReservationStatus(Long reservationId, String status) {
        log.info("Updating reservation {} status to {}", reservationId, status);
        
        TableReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        TableReservation.ReservationStatus newStatus = TableReservation.ReservationStatus.valueOf(status);
        reservation.setStatus(newStatus);
        
        switch (newStatus) {
            case CHECKED_IN:
                reservation.setCheckedInAt(LocalDateTime.now());
                // Mark table as occupied
                if (reservation.getTable() != null) {
                    RestaurantTable table = reservation.getTable();
                    table.setIsAvailable(false);
                    tableRepository.save(table);
                }
                break;
            case COMPLETED:
                reservation.setCheckedOutAt(LocalDateTime.now());
                // Mark table as available
                if (reservation.getTable() != null) {
                    RestaurantTable table = reservation.getTable();
                    table.setIsAvailable(true);
                    tableRepository.save(table);
                }
                break;
            case CANCELLED:
                reservation.setCancelledAt(LocalDateTime.now());
                break;
        }
        
        reservation = reservationRepository.save(reservation);
        return mapToReservationDTO(reservation);
    }
    
    @Transactional
    public TableReservationDTO confirmReservation(Long reservationId) {
        return updateReservationStatus(reservationId, "CONFIRMED");
    }
    
    @Transactional
    public TableReservationDTO checkInReservation(Long reservationId) {
        return updateReservationStatus(reservationId, "CHECKED_IN");
    }
    
    @Transactional
    public TableReservationDTO checkOutReservation(Long reservationId) {
        return updateReservationStatus(reservationId, "COMPLETED");
    }
    
    @Transactional
    public TableReservationDTO cancelReservation(Long reservationId, String reason) {
        log.info("Cancelling reservation: {}", reservationId);
        
        TableReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        reservation.setStatus(TableReservation.ReservationStatus.CANCELLED);
        reservation.setCancellationReason(reason);
        reservation.setCancelledAt(LocalDateTime.now());
        
        reservation = reservationRepository.save(reservation);
        return mapToReservationDTO(reservation);
    }
    
    public TableReservationDTO getReservationById(Long id) {
        TableReservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return mapToReservationDTO(reservation);
    }
    
    public TableReservationDTO getReservationByNumber(String reservationNumber) {
        TableReservation reservation = reservationRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return mapToReservationDTO(reservation);
    }
    
    public List<TableReservationDTO> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::mapToReservationDTO)
                .collect(Collectors.toList());
    }
    
    public List<TableReservationDTO> getUpcomingReservations() {
        return reservationRepository.findUpcomingReservations(LocalDateTime.now()).stream()
                .map(this::mapToReservationDTO)
                .collect(Collectors.toList());
    }
    
    public List<TableReservationDTO> getReservationsByDate(LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
        
        return reservationRepository.findReservationsBetweenDates(startOfDay, endOfDay).stream()
                .map(this::mapToReservationDTO)
                .collect(Collectors.toList());
    }
    
    public List<TableReservationDTO> getReservationsByStatus(String status) {
        return reservationRepository.findByStatus(TableReservation.ReservationStatus.valueOf(status)).stream()
                .map(this::mapToReservationDTO)
                .collect(Collectors.toList());
    }
    
    public boolean isTableAvailable(Long tableId, LocalDateTime reservationTime, Integer durationMinutes) {
        LocalDateTime endTime = reservationTime.plusMinutes(durationMinutes);
        
        List<TableReservation> conflictingReservations = reservationRepository
                .findActiveReservationsForTable(tableId, reservationTime, endTime);
        
        return conflictingReservations.isEmpty();
    }
    
    public List<RestaurantTable> getAvailableTables(LocalDateTime reservationTime, Integer numberOfGuests, Integer durationMinutes) {
        List<RestaurantTable> allTables = tableRepository.findByIsAvailableTrue();
        
        return allTables.stream()
                .filter(table -> table.getCapacity() >= numberOfGuests)
                .filter(table -> isTableAvailable(table.getId(), reservationTime, durationMinutes))
                .collect(Collectors.toList());
    }
    
    // ==================== HELPER METHODS ====================
    
    private String generateReservationNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "RES-" + timestamp;
    }
    
    private TableReservationDTO mapToReservationDTO(TableReservation reservation) {
        return TableReservationDTO.builder()
                .id(reservation.getId())
                .reservationNumber(reservation.getReservationNumber())
                .userId(reservation.getUserId())
                .customerName(reservation.getCustomerName())
                .customerPhone(reservation.getCustomerPhone())
                .customerEmail(reservation.getCustomerEmail())
                .tableId(reservation.getTable() != null ? reservation.getTable().getId() : null)
                .tableNumber(reservation.getTable() != null ? reservation.getTable().getTableNumber() : null)
                .tableName(reservation.getTable() != null ? reservation.getTable().getTableName() : null)
                .numberOfGuests(reservation.getNumberOfGuests())
                .reservationDateTime(reservation.getReservationDateTime())
                .durationMinutes(reservation.getDurationMinutes())
                .status(reservation.getStatus().name())
                .specialRequests(reservation.getSpecialRequests())
                .occasion(reservation.getOccasion())
                .requiresHighChair(reservation.getRequiresHighChair())
                .requiresWheelchairAccess(reservation.getRequiresWheelchairAccess())
                .checkedInAt(reservation.getCheckedInAt())
                .checkedOutAt(reservation.getCheckedOutAt())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
