package com.ridemate.repository;

import com.ridemate.model.Payment;
import com.ridemate.model.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPayerId(Long payerId);
    List<Payment> findByPayeeId(Long payeeId);
    List<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);
    List<Payment> findByStatus(PaymentStatus status);
}
