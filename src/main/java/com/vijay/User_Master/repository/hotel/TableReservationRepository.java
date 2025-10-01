package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.TableReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TableReservationRepository extends JpaRepository<TableReservation, Long> {
    
    // ========== MULTI-TENANT QUERIES ==========
    
    List<TableReservation> findByBusinessId(Long businessId);
    
    List<TableReservation> findByBusinessIdAndStatus(Long businessId, TableReservation.ReservationStatus status);
    
    List<TableReservation> findByBusinessIdAndUserId(Long businessId, Long userId);
    
    @Query("SELECT tr FROM TableReservation tr WHERE tr.businessId = :businessId AND tr.reservationDateTime BETWEEN :startDate AND :endDate")
    List<TableReservation> findByBusinessIdAndReservationDateBetween(@Param("businessId") Long businessId,
                                                                      @Param("startDate") LocalDateTime startDate,
                                                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT tr FROM TableReservation tr WHERE tr.businessId = :businessId AND tr.table.id = :tableId AND tr.reservationDateTime BETWEEN :startDate AND :endDate AND tr.status NOT IN ('CANCELLED', 'NO_SHOW', 'COMPLETED')")
    List<TableReservation> findActiveReservationsByBusinessIdAndTable(@Param("businessId") Long businessId,
                                                                       @Param("tableId") Long tableId,
                                                                       @Param("startDate") LocalDateTime startDate,
                                                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT tr FROM TableReservation tr WHERE tr.businessId = :businessId AND tr.reservationDateTime >= :now AND tr.status = 'CONFIRMED' ORDER BY tr.reservationDateTime ASC")
    List<TableReservation> findUpcomingReservationsByBusinessId(@Param("businessId") Long businessId, @Param("now") LocalDateTime now);
    
    @Query("SELECT tr FROM TableReservation tr WHERE tr.businessId = :businessId AND DATE(tr.reservationDateTime) = CURRENT_DATE")
    List<TableReservation> findTodaysReservationsByBusinessId(@Param("businessId") Long businessId);
    
    Long countByBusinessId(Long businessId);
    
    Long countByBusinessIdAndStatus(Long businessId, TableReservation.ReservationStatus status);
    
    // ========== ORIGINAL QUERIES ==========
    
    Optional<TableReservation> findByReservationNumber(String reservationNumber);
    
    List<TableReservation> findByUserId(Long userId);
    
    List<TableReservation> findByStatus(TableReservation.ReservationStatus status);
    
    @Query("SELECT tr FROM TableReservation tr WHERE tr.reservationDateTime BETWEEN :startDate AND :endDate")
    List<TableReservation> findReservationsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT tr FROM TableReservation tr WHERE tr.table.id = :tableId AND tr.reservationDateTime BETWEEN :startDate AND :endDate AND tr.status NOT IN ('CANCELLED', 'NO_SHOW', 'COMPLETED')")
    List<TableReservation> findActiveReservationsForTable(@Param("tableId") Long tableId,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT tr FROM TableReservation tr WHERE tr.reservationDateTime >= :now AND tr.status = 'CONFIRMED' ORDER BY tr.reservationDateTime ASC")
    List<TableReservation> findUpcomingReservations(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(tr) FROM TableReservation tr WHERE tr.userId = :userId AND tr.status = 'NO_SHOW'")
    Long countNoShowsByUser(@Param("userId") Long userId);
}
