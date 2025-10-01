package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.GuestSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuestSessionRepository extends JpaRepository<GuestSession, Long> {
    
    // Find by session token (for guest authentication)
    Optional<GuestSession> findBySessionToken(String sessionToken);
    
    // Find by business ID
    List<GuestSession> findByBusinessId(Long businessId);
    
    // Find by business and status
    List<GuestSession> findByBusinessIdAndStatus(Long businessId, GuestSession.SessionStatus status);
    
    // Find active sessions by business
    @Query("SELECT gs FROM GuestSession gs WHERE gs.businessId = :businessId AND gs.status = 'ACTIVE' AND gs.expiresAt > :now")
    List<GuestSession> findActiveSessionsByBusinessId(@Param("businessId") Long businessId, @Param("now") LocalDateTime now);
    
    // Find by table and status
    List<GuestSession> findByTableIdAndStatus(Long tableId, GuestSession.SessionStatus status);
    
    // Find active session for table
    @Query("SELECT gs FROM GuestSession gs WHERE gs.tableId = :tableId AND gs.status = 'ACTIVE' AND gs.expiresAt > :now")
    Optional<GuestSession> findActiveSessionForTable(@Param("tableId") Long tableId, @Param("now") LocalDateTime now);
    
    // Count sessions by business
    Long countByBusinessId(Long businessId);
    
    Long countByBusinessIdAndStatus(Long businessId, GuestSession.SessionStatus status);
    
    // Find expired sessions
    @Query("SELECT gs FROM GuestSession gs WHERE gs.status = 'ACTIVE' AND gs.expiresAt < :now")
    List<GuestSession> findExpiredSessions(@Param("now") LocalDateTime now);
    
    // Today's sessions by business
    @Query("SELECT gs FROM GuestSession gs WHERE gs.businessId = :businessId AND DATE(gs.createdAt) = CURRENT_DATE")
    List<GuestSession> findTodaysSessionsByBusinessId(@Param("businessId") Long businessId);
}
