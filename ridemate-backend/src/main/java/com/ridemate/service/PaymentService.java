package com.ridemate.service;

import com.ridemate.model.Booking;
import com.ridemate.model.Payment;
import com.ridemate.model.User;
import com.ridemate.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    /**
     * Create a payment order for Razorpay
     * Note: This is a simplified version. In production, you'd use Razorpay SDK
     */
    @Transactional
    public Payment createPaymentOrder(Booking booking, User payer, User payee, Double amount) {
        try {
            // Generate a unique order ID
            String orderId = "order_" + System.currentTimeMillis();

            Payment payment = Payment.builder()
                    .booking(booking)
                    .payer(payer)
                    .payee(payee)
                    .amount(amount)
                    .status(Payment.PaymentStatus.PENDING)
                    .method(Payment.PaymentMethod.RAZORPAY)
                    .razorpayOrderId(orderId)
                    .build();

            payment = paymentRepository.save(payment);
            log.info("Created payment order {} for booking {}", orderId, booking.getId());

            return payment;
        } catch (Exception e) {
            log.error("Error creating payment order for booking {}", booking.getId(), e);
            throw new RuntimeException("Failed to create payment order", e);
        }
    }

    /**
     * Verify Razorpay payment signature
     */
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            String message = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(razorpayKeySecret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(message.getBytes());
            String generatedSignature = Base64.getEncoder().encodeToString(hash);

            return generatedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying payment signature", e);
            return false;
        }
    }

    /**
     * Capture payment after successful verification
     */
    @Transactional
    public Payment capturePayment(String orderId, String paymentId, String signature) {
        Payment payment = paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        if (verifyPaymentSignature(orderId, paymentId, signature)) {
            payment.setRazorpayPaymentId(paymentId);
            payment.setRazorpaySignature(signature);
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setCompletedAt(LocalDateTime.now());

            payment = paymentRepository.save(payment);
            log.info("Payment captured successfully: {}", paymentId);

            return payment;
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason("Invalid signature");
            paymentRepository.save(payment);

            throw new RuntimeException("Payment verification failed");
        }
    }

    /**
     * Put payment on hold until ride completion
     */
    @Transactional
    public Payment holdPayment(Payment payment) {
        payment.setStatus(Payment.PaymentStatus.ON_HOLD);
        return paymentRepository.save(payment);
    }

    /**
     * Release held payment after ride completion
     */
    @Transactional
    public Payment releasePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == Payment.PaymentStatus.ON_HOLD) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setCompletedAt(LocalDateTime.now());
            return paymentRepository.save(payment);
        }

        throw new RuntimeException("Payment is not on hold");
    }

    /**
     * Refund payment (e.g., on ride cancellation)
     */
    @Transactional
    public Payment refundPayment(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // In production, call Razorpay refund API here
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setRefundedAt(LocalDateTime.now());
        payment.setFailureReason(reason);

        payment = paymentRepository.save(payment);
        log.info("Payment {} refunded: {}", paymentId, reason);

        return payment;
    }

    /**
     * Create cash payment record
     */
    @Transactional
    public Payment createCashPayment(Booking booking, User payer, User payee, Double amount) {
        Payment payment = Payment.builder()
                .booking(booking)
                .payer(payer)
                .payee(payee)
                .amount(amount)
                .status(Payment.PaymentStatus.PENDING)
                .method(Payment.PaymentMethod.CASH)
                .build();

        return paymentRepository.save(payment);
    }

    /**
     * Mark cash payment as completed
     */
    @Transactional
    public Payment completeCashPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setCompletedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public List<Payment> getUserPayments(Long userId) {
        List<Payment> sent = paymentRepository.findByPayerId(userId);
        List<Payment> received = paymentRepository.findByPayeeId(userId);

        sent.addAll(received);
        return sent;
    }

    public List<Payment> getBookingPayments(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}
