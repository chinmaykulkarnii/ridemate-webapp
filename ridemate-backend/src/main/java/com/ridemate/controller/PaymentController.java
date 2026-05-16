package com.ridemate.controller;

import com.ridemate.model.Payment;
import com.ridemate.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/verify")
    public ResponseEntity<Payment> verifyPayment(
            @RequestBody Map<String, String> request) {
        String orderId = request.get("razorpay_order_id");
        String paymentId = request.get("razorpay_payment_id");
        String signature = request.get("razorpay_signature");

        Payment payment = paymentService.capturePayment(orderId, paymentId, signature);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{id}/complete-cash")
    public ResponseEntity<Payment> completeCashPayment(@PathVariable Long id) {
        Payment payment = paymentService.completeCashPayment(id);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> refundPayment(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        Payment payment = paymentService.refundPayment(id, reason);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/my-payments")
    public ResponseEntity<List<Payment>> getMyPayments(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<Payment> payments = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Payment>> getBookingPayments(@PathVariable Long bookingId) {
        List<Payment> payments = paymentService.getBookingPayments(bookingId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }
}
