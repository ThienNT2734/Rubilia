import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useLocation, useNavigate } from 'react-router-dom';
import { getCart, clearCart } from '../utils/cartUtils';
import QRCode from 'react-qr-code';
import '../css/Checkout.css';

const Checkout = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    address: '',
    paymentMethod: 'cod',
  });
  const [cartItems, setCartItems] = useState([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [paymentInfo, setPaymentInfo] = useState(null);
  const [isProcessingPayment, setIsProcessingPayment] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const status = params.get('status');
    if (status) {
      // This is a return from a payment gateway
      if (status === 'success') {
        setSuccess(true);
        clearCart();
        setTimeout(() => navigate('/'), 5000);
      } else {
        const message = params.get('message') || 'Lỗi không xác định.';
        setError(`Thanh toán thất bại. Lý do: ${decodeURIComponent(message.replace(/\+/g, ' '))}`);
      }
      // Clean up the URL
      navigate('/checkout', { replace: true });
    }

    const items = getCart().map(item => ({
      productId: item.id,
      quantity: item.quantity,
      price: item.salePrice,
    }));
    setCartItems(items);
  }, [location.search, navigate]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess(false);
    setPaymentInfo(null);
    setIsProcessingPayment(true);

    const subtotal = cartItems.reduce((total, item) => total + item.price * item.quantity, 0);
    const shippingFee = 30000;
    const orderTotal = subtotal + shippingFee;

    const orderData = {
      customer: {
        name: formData.name,
        email: formData.email,
        phone: formData.phone,
        address: formData.address,
      },
      cartItems,
      couponCode: localStorage.getItem('couponCode') || null,
      totalPrice: orderTotal,
      shippingFee,
      discount: 0,
      paymentMethod: formData.paymentMethod,
    };

    try {
      const orderResponse = await axios.post('https://rubilia.store/api/orders', orderData);
      if (orderResponse.status === 200) {
        const orderId = orderResponse.data?.orderId || null;
        if (formData.paymentMethod === 'vnpay_web' || formData.paymentMethod === 'vnpay_qr' || formData.paymentMethod.startsWith('momo')) {
          if (!orderId) {
            throw new Error('Không nhận được mã đơn hàng từ backend');
          }

          if (formData.paymentMethod.startsWith('momo')) {
            const paymentResponse = await axios.post('https://rubilia.store/api/momo/create', {
              orderId,
              amount: orderData.totalPrice,
              paymentType: formData.paymentMethod,
            });
            const paymentUrl = paymentResponse.data?.paymentUrl;
            if (!paymentUrl) {
              throw new Error('Không nhận được đường dẫn thanh toán MoMo');
            }
            window.location.href = paymentUrl;
            return;
          }

          const payType = formData.paymentMethod === 'vnpay_qr' ? 'QR' : 'WEB';
          const paymentResponse = await axios.post('https://rubilia.store/api/vnpay/create', {
            orderId,
            amount: orderData.totalPrice,
            paymentType: payType,
          });

          const paymentUrl = paymentResponse.data?.paymentUrl || null;
          const qrData = paymentResponse.data?.qrData || paymentUrl;

          setPaymentInfo({
            orderId,
            paymentMethod: formData.paymentMethod,
            paymentUrl,
            qrData,
          });

          if (formData.paymentMethod === 'vnpay_web' && paymentUrl) {
            window.location.href = paymentUrl;
            return;
          }

          setSuccess(formData.paymentMethod !== 'vnpay_qr');
          return;
        }

        setSuccess(true);
        clearCart();
        setTimeout(() => {
          navigate('/');
        }, 5000);
      }
    } catch (err) {
      const errorMessage = err.response?.data?.message || err.response?.data || err.message || 'Lỗi khi đặt hàng';
      setError(typeof errorMessage === 'string' ? errorMessage : JSON.stringify(errorMessage));
      console.error(err);
    } finally {
      setIsProcessingPayment(false);
    }
  };

  return (
    <div className="checkout-container">
      <h2>Thanh Toán</h2>
      {error && <div className="error-message">{error}</div>}
      {success && (
        <div className="success-popup">
          <div className="success-popup-content">
            <h3>Đặt hàng thành công!</h3>
            <p>Sẽ chuyển hướng về trang chủ sau 5 giây...</p>
            <button onClick={() => navigate('/')}>Đóng</button>
          </div>
        </div>
      )}
      <div className="checkout-content">
        <div className="customer-info">
          <h3>Thông Tin Khách Hàng</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="name">Họ và Tên *</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="email">Email *</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="phone">Số Điện Thoại *</label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="address">Địa Chỉ *</label>
              <input
                type="text"
                id="address"
                name="address"
                value={formData.address}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="paymentMethod">Phương Thức Thanh Toán</label>
              <select
                id="paymentMethod"
                name="paymentMethod"
                value={formData.paymentMethod}
                onChange={handleChange}
              >
                <option value="cod">Thanh toán khi nhận hàng</option>
                <option value="vnpay_web">VNPay Web</option>
                <option value="vnpay_qr">VNPay QR</option>
                <option value="momo_wallet">MoMo - ví/MoMo QR</option>
                <option value="momo_card">MoMo - thẻ/ATM</option>
              </select>
            </div>
            <button type="submit">Đặt Hàng</button>
          </form>
        </div>
        <div className="order-summary">
          <h3>Tóm Tắt Đơn Hàng</h3>
          {cartItems.map((item, index) => (
            <div key={index} className="cart-item">
              <div>
                <p>Product ID: {item.productId}</p>
                <p>Số lượng: {item.quantity}</p>
                <p>Giá: {(item.price * item.quantity).toLocaleString('vi-VN')} đ</p>
              </div>
            </div>
          ))}
          <div className="price-details">
            <p><span>Tạm tính:</span> <span>{cartItems.reduce((total, item) => total + item.price * item.quantity, 0).toLocaleString('vi-VN')} đ</span></p>
            <p><span>Phí vận chuyển:</span> <span>30,000 đ</span></p>
            <p><span>Tổng cộng:</span> <span>{(cartItems.reduce((total, item) => total + item.price * item.quantity, 0) + 30000).toLocaleString('vi-VN')} đ</span></p>
          </div>
          {isProcessingPayment && (
            <div className="payment-status-box">
              <h3>Đang tạo đường dẫn thanh toán...</h3>
              <p>Vui lòng chờ trong giây lát.</p>
            </div>
          )}
          {paymentInfo && paymentInfo.paymentMethod === 'vnpay_qr' && (
            <div className="payment-qr-box">
              <h3>Thanh toán bằng VNPay QR</h3>
              <p>Quét mã QR bên dưới để thanh toán đơn hàng {paymentInfo.orderId}.</p>
              <div className="qr-code-wrapper">
                <QRCode value={paymentInfo.qrData || paymentInfo.paymentUrl} size={260} />
              </div>
              <p className="qr-help">Nếu không quét được mã, hãy mở liên kết dưới đây:</p>
              <a href={paymentInfo.paymentUrl} target="_blank" rel="noreferrer">Mở liên kết thanh toán</a>
              <p className="qr-note">Sau khi thanh toán xong, hãy kiểm tra lại trang lịch sử đơn hàng hoặc truy cập lại trang chủ.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Checkout;