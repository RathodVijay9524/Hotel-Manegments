package com.vijay.User_Master.service.hotel;

import com.vijay.User_Master.dto.hotel.CreatePaymentRequest;
import com.vijay.User_Master.dto.hotel.PaymentDTO;
import com.vijay.User_Master.entity.hotel.Order;
import com.vijay.User_Master.entity.hotel.Payment;
import com.vijay.User_Master.repository.hotel.OrderRepository;
import com.vijay.User_Master.repository.hotel.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    
    @Transactional
    public PaymentDTO createPayment(CreatePaymentRequest request) {
        log.info("Creating payment for order: {}", request.getOrderId());
        
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Check if payment already exists
        paymentRepository.findByOrderId(order.getId()).ifPresent(p -> {
            throw new RuntimeException("Payment already exists for this order");
        });
        
        // Generate transaction ID
        String transactionId = generateTransactionId();
        
        Payment payment = Payment.builder()
                .order(order)
                .transactionId(transactionId)
                .amount(request.getAmount())
                .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .status(Payment.PaymentStatus.PENDING)
                .paymentDetails(request.getPaymentDetails())
                .build();
        
        payment = paymentRepository.save(payment);
        
        log.info("Payment created with transaction ID: {}", transactionId);
        return mapToPaymentDTO(payment);
    }
    
    @Transactional
    public PaymentDTO processPayment(Long paymentId) {
        log.info("Processing payment: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        // Simulate payment processing
        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        paymentRepository.save(payment);
        
        // In real scenario, integrate with payment gateway here
        // For now, mark as completed
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        
        payment = paymentRepository.save(payment);
        
        // Update order status
        Order order = payment.getOrder();
        if (order.getStatus() == Order.OrderStatus.PENDING) {
            order.setStatus(Order.OrderStatus.CONFIRMED);
            orderRepository.save(order);
        }
        
        log.info("Payment processed successfully");
        return mapToPaymentDTO(payment);
    }
    
    @Transactional
    public PaymentDTO updatePaymentStatus(Long paymentId, String status) {
        log.info("Updating payment {} status to {}", paymentId, status);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(Payment.PaymentStatus.valueOf(status));
        
        if (status.equals("COMPLETED")) {
            payment.setPaidAt(LocalDateTime.now());
            
            // Update order status
            Order order = payment.getOrder();
            if (order.getStatus() == Order.OrderStatus.PENDING) {
                order.setStatus(Order.OrderStatus.CONFIRMED);
                orderRepository.save(order);
            }
        }
        
        payment = paymentRepository.save(payment);
        return mapToPaymentDTO(payment);
    }
    
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return mapToPaymentDTO(payment);
    }
    
    public PaymentDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order"));
        return mapToPaymentDTO(payment);
    }
    
    public PaymentDTO getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return mapToPaymentDTO(payment);
    }
    
    public List<PaymentDTO> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(Payment.PaymentStatus.valueOf(status)).stream()
                .map(this::mapToPaymentDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PaymentDTO refundPayment(Long paymentId) {
        log.info("Refunding payment: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }
        
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);
        
        // Update order status
        Order order = payment.getOrder();
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        log.info("Payment refunded successfully");
        return mapToPaymentDTO(payment);
    }
    
    // ==================== HELPER METHODS ====================
    
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private PaymentDTO mapToPaymentDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .orderNumber(payment.getOrder().getOrderNumber())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .status(payment.getStatus().name())
                .paymentDetails(payment.getPaymentDetails())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
