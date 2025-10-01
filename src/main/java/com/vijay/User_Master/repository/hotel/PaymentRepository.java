package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByOrderId(Long orderId);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentMethod = :method AND p.status = :status")
    List<Payment> findByPaymentMethodAndStatus(@Param("method") Payment.PaymentMethod method,
                                               @Param("status") Payment.PaymentStatus status);
}
