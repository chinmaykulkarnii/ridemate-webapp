import React, { useState } from 'react';
import { paymentService } from '../../services/paymentService';
import { useToast } from '../../context/ToastContext';
import '../../styles/PaymentForm.css';

const PaymentForm = ({ booking, amount, onSuccess, onCancel }) => {
  const [paymentMethod, setPaymentMethod] = useState('online'); // 'online' or 'cash'
  const [isProcessing, setIsProcessing] = useState(false);
  const toast = useToast();

  const loadRazorpayScript = () => {
    return new Promise((resolve) => {
      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.onload = () => resolve(true);
      script.onerror = () => resolve(false);
      document.body.appendChild(script);
    });
  };

  const handleOnlinePayment = async () => {
    setIsProcessing(true);

    try {
      // Load Razorpay script
      const scriptLoaded = await loadRazorpayScript();
      if (!scriptLoaded) {
        toast.error('Failed to load payment gateway. Please try again.');
        setIsProcessing(false);
        return;
      }

      // Create order on backend (you may need to add this endpoint)
      // For now, we'll use mock order details
      const orderData = {
        amount: amount * 100, // Razorpay expects amount in paise
        currency: 'INR',
        receipt: `booking_${booking.id}`,
      };

      const options = {
        key: process.env.REACT_APP_RAZORPAY_KEY_ID || 'rzp_test_dummy_key', // Replace with actual key
        amount: orderData.amount,
        currency: orderData.currency,
        name: 'RideMate',
        description: `Payment for Ride Booking #${booking.id}`,
        image: '/logo.png', // Your logo
        handler: async function (response) {
          try {
            // Verify payment on backend
            const verificationData = {
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature,
              bookingId: booking.id,
              amount: amount,
            };

            const result = await paymentService.verifyPayment(verificationData);
            toast.success('Payment successful!');
            setIsProcessing(false);
            onSuccess && onSuccess(result);
          } catch (error) {
            console.error('Payment verification failed:', error);
            toast.error('Payment verification failed. Please contact support.');
            setIsProcessing(false);
          }
        },
        prefill: {
          name: booking.passenger?.firstName + ' ' + booking.passenger?.lastName,
          email: booking.passenger?.email,
          contact: booking.passenger?.phone,
        },
        theme: {
          color: '#667eea',
        },
        modal: {
          ondismiss: function() {
            setIsProcessing(false);
            toast.warning('Payment cancelled');
          }
        }
      };

      const razorpay = new window.Razorpay(options);
      razorpay.open();
    } catch (error) {
      console.error('Payment error:', error);
      toast.error('Failed to initiate payment. Please try again.');
      setIsProcessing(false);
    }
  };

  const handleCashPayment = async () => {
    setIsProcessing(true);

    try {
      // Mark as cash payment (will be confirmed after ride completion)
      toast.info('Cash payment will be collected at the end of the ride');
      setIsProcessing(false);
      onSuccess && onSuccess({ method: 'cash', status: 'pending' });
    } catch (error) {
      console.error('Error processing cash payment:', error);
      toast.error('Failed to process payment option');
      setIsProcessing(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (paymentMethod === 'online') {
      handleOnlinePayment();
    } else {
      handleCashPayment();
    }
  };

  return (
    <div className="payment-form-container">
      <h2>Payment Details</h2>

      <div className="payment-summary">
        <div className="summary-row">
          <span>Booking ID:</span>
          <strong>#{booking.id}</strong>
        </div>
        <div className="summary-row">
          <span>Route:</span>
          <strong>{booking.ride?.origin} → {booking.ride?.destination}</strong>
        </div>
        <div className="summary-row">
          <span>Seats:</span>
          <strong>{booking.seatsBooked}</strong>
        </div>
        <div className="summary-row total">
          <span>Total Amount:</span>
          <strong>₹{amount}</strong>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="payment-form">
        <div className="payment-methods">
          <h3>Select Payment Method</h3>

          <label className={`payment-method-option ${paymentMethod === 'online' ? 'selected' : ''}`}>
            <input
              type="radio"
              name="paymentMethod"
              value="online"
              checked={paymentMethod === 'online'}
              onChange={(e) => setPaymentMethod(e.target.value)}
            />
            <div className="method-details">
              <span className="method-icon">💳</span>
              <div>
                <div className="method-name">Online Payment</div>
                <div className="method-description">Pay securely with UPI, Cards, or Netbanking</div>
              </div>
            </div>
          </label>

          <label className={`payment-method-option ${paymentMethod === 'cash' ? 'selected' : ''}`}>
            <input
              type="radio"
              name="paymentMethod"
              value="cash"
              checked={paymentMethod === 'cash'}
              onChange={(e) => setPaymentMethod(e.target.value)}
            />
            <div className="method-details">
              <span className="method-icon">💵</span>
              <div>
                <div className="method-name">Cash Payment</div>
                <div className="method-description">Pay cash to the driver after the ride</div>
              </div>
            </div>
          </label>
        </div>

        <div className="payment-actions">
          <button
            type="button"
            className="btn btn-cancel"
            onClick={onCancel}
            disabled={isProcessing}
          >
            Cancel
          </button>
          <button
            type="submit"
            className="btn btn-pay"
            disabled={isProcessing}
          >
            {isProcessing ? (
              <span>Processing...</span>
            ) : (
              <span>
                {paymentMethod === 'online' ? 'Pay Now' : 'Confirm Booking'}
              </span>
            )}
          </button>
        </div>
      </form>

      <div className="payment-security">
        <span className="security-icon">🔒</span>
        <span>Your payment information is secure and encrypted</span>
      </div>
    </div>
  );
};

export default PaymentForm;
