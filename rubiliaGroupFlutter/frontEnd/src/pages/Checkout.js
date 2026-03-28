import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { getCart, clearCart } from '../utils/cartUtils';
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
  const navigate = useNavigate();

  useEffect(() => {
    const items = getCart().map(item => ({
      productId: item.id,
      quantity: item.quantity,
      price: item.salePrice,
    }));
    setCartItems(items);
  }, []);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess(false);

    const orderData = {
      customer: {
        name: formData.name,
        email: formData.email,
        phone: formData.phone,
        address: formData.address,
      },
      cartItems,
      couponCode: localStorage.getItem('couponCode') || null,
      totalPrice: cartItems.reduce((total, item) => total + item.price * item.quantity, 0),
      shippingFee: 30000,
      discount: 0,
      paymentMethod: formData.paymentMethod,
    };

    try {
      const response = await axios.post('http://localhost:8080/api/orders', orderData);
      if (response.status === 200) {
        setSuccess(true);
        clearCart();
        setTimeout(() => {
          navigate('/');
        }, 5000);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Lỗi khi đặt hàng');
      console.error(err);
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
                <option value="bank">Chuyển khoản ngân hàng</option>
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
        </div>
      </div>
    </div>
  );
};

export default Checkout;