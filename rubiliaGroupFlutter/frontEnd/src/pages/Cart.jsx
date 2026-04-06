import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getCart, updateCartQuantity, removeFromCart } from '../utils/cartUtils';
import '../css/Cart.css';

const Cart = () => {
    const [cartItems, setCartItems] = useState([]);
    const [couponCode, setCouponCode] = useState('');
    const [discount, setDiscount] = useState(0);
    const [notification, setNotification] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        setIsLoading(true);
        const cart = getCart();
        setCartItems(cart);
        const storedCoupon = localStorage.getItem('couponCode');
        if (storedCoupon) {
            setCouponCode(storedCoupon);
            if (storedCoupon === 'RUBILIA10') setDiscount(0.1);
            else if (storedCoupon === 'RUBILIA20') setDiscount(0.2);
            else setDiscount(0);
        }
        setIsLoading(false);

        const handleStorageChange = () => {
            const updatedCart = getCart();
            setCartItems(updatedCart);
            const storedCoupon = localStorage.getItem('couponCode');
            if (storedCoupon) {
                setCouponCode(storedCoupon);
                if (storedCoupon === 'RUBILIA10') setDiscount(0.1);
                else if (storedCoupon === 'RUBILIA20') setDiscount(0.2);
                else setDiscount(0);
            }
        };

        window.addEventListener('storage', handleStorageChange);
        return () => window.removeEventListener('storage', handleStorageChange);
    }, []);

    useEffect(() => {
        if (notification) {
            const timer = setTimeout(() => setNotification(null), 3000);
            return () => clearTimeout(timer);
        }
    }, [notification]);

    const showNotification = (message) => {
        setNotification(message);
    };

    const handleUpdateQuantity = (productId, action) => {
        const updatedCart = updateCartQuantity(productId, action);
        setCartItems(updatedCart);
        showNotification(`Đã ${action === 'increase' ? 'tăng' : 'giảm'} số lượng sản phẩm.`);
    };

    const handleRemoveFromCart = (productId) => {
        const updatedCart = removeFromCart(productId);
        setCartItems(updatedCart);
        showNotification('Đã xóa sản phẩm khỏi giỏ hàng.');
    };

    const handleApplyCoupon = () => {
        const code = couponCode.trim();
        if (code === 'RUBILIA10') {
            setDiscount(0.1);
            localStorage.setItem('couponCode', code);
            showNotification('Áp dụng mã giảm 10% thành công!');
        } else if (code === 'RUBILIA20') {
            setDiscount(0.2);
            localStorage.setItem('couponCode', code);
            showNotification('Áp dụng mã giảm 20% thành công!');
        } else {
            setDiscount(0);
            localStorage.removeItem('couponCode');
            showNotification('Mã khuyến mãi không hợp lệ.');
        }
    };

    const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0);
    const totalPrice = cartItems.reduce((sum, item) => sum + (item.salePrice || 0) * item.quantity, 0);
    const discountedPrice = totalPrice * (1 - discount);

    if (isLoading) {
        return (
            <div className="container my-5">
                <div className="skeleton skeleton-title"></div>
                <div className="cart-table">
                    {[...Array(3)].map((_, i) => (
                        <div key={i} className="skeleton skeleton-row"></div>
                    ))}
                </div>
                <div className="skeleton skeleton-summary"></div>
            </div>
        );
    }

    return (
        <div className="container my-5">
            {notification && (
                <div className="notification-popup">
                    {notification}
                </div>
            )}
            <h1 className="cart-title">Giỏ hàng</h1>

            {cartItems.length === 0 ? (
                <div className="empty-cart">
                    <i className="fas fa-shopping-cart"></i>
                    <p>Giỏ hàng của bạn đang trống.</p>
                    <Link to="/" className="btn-continue-shopping">Tiếp tục mua sắm</Link>
                </div>
            ) : (
                <>
                    <div className="cart-table">
                        <div className="table-header">
                            <span>Sản phẩm</span>
                            <span>Đơn giá</span>
                            <span>Số lượng</span>
                            <span>Tổng cộng</span>
                            <span>Thao tác</span>
                        </div>
                        {cartItems.map(item => (
                            <div key={item.id} className="table-row">
                                <span className="product-name">{item.productName || 'Không có tên'}</span>
                                <span className="price">₫{(item.salePrice || 0).toLocaleString()}</span>
                                <span className="quantity-control">
                                    <button
                                        className="quantity-btn"
                                        onClick={() => handleUpdateQuantity(item.id, 'decrease')}
                                    >
                                        -
                                    </button>
                                    <span className="quantity-value">{item.quantity}</span>
                                    <button
                                        className="quantity-btn"
                                        onClick={() => handleUpdateQuantity(item.id, 'increase')}
                                    >
                                        +
                                    </button>
                                </span>
                                <span className="subtotal">₫{((item.salePrice || 0) * item.quantity).toLocaleString()}</span>
                                <span>
                                    <button
                                        className="remove-btn"
                                        onClick={() => handleRemoveFromCart(item.id)}
                                    >
                                        <i className="fas fa-trash"></i> Xóa
                                    </button>
                                </span>
                            </div>
                        ))}
                    </div>

                    <div className="coupon-section">
                        <div className="coupon-input-group">
                            <input
                                type="text"
                                className="coupon-input"
                                placeholder="Nhập mã khuyến mãi..."
                                value={couponCode}
                                onChange={(e) => setCouponCode(e.target.value)}
                            />
                            <button className="apply-coupon-btn" onClick={handleApplyCoupon}>
                                Áp dụng
                            </button>
                        </div>
                        {discount > 0 && (
                            <span className="discount-message">
                                Đã áp dụng mã giảm {discount * 100}%!
                            </span>
                        )}
                    </div>

                    <div className="cart-summary">
                        <h5>Tổng sản phẩm: {totalItems}</h5>
                        <h4>
                            Tổng tiền:{' '}
                            {discount > 0 ? (
                                <>
                                    <span className="original-price">
                                        ₫{totalPrice.toLocaleString()}
                                    </span>
                                    <span className="discounted-price">
                                        ₫{discountedPrice.toLocaleString()}
                                    </span>
                                </>
                            ) : (
                                <span className="total-price">
                                    ₫{totalPrice.toLocaleString()}
                                </span>
                            )}
                        </h4>
                    </div>

                    <div className="cart-actions">
                        <Link to="/" className="btn-continue-shopping">
                            <i className="fas fa-shopping-bag"></i> Tiếp tục mua sắm
                        </Link>
                        <Link to="/checkout" className="btn-checkout">
                            <i className="fas fa-credit-card"></i> Thanh toán
                        </Link>
                    </div>
                </>
            )}
        </div>
    );
};

export default Cart;