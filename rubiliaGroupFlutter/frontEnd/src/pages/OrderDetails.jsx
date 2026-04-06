import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import '../css/orderdetail.css';

const OrderDetails = () => {
    const { orderId } = useParams();
    const navigate = useNavigate();
    const [order, setOrder] = useState(null);
    const [orderItems, setOrderItems] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchOrderDetails = async () => {
            try {
                const orderResponse = await axios.get(`https://rubilia.store/api/orders/${orderId}`);
                setOrder(orderResponse.data);

                const itemsResponse = await axios.get(`https://rubilia.store/api/orders/${orderId}/items`);
                setOrderItems(itemsResponse.data);
            } catch (err) {
                console.error('Lỗi lấy chi tiết đơn hàng:', err.response?.data || err.message);
                const errorMessage = err.response?.data?.error || err.response?.data?.message || err.message || 'Lỗi khi lấy chi tiết đơn hàng.';
                setError(errorMessage);
            }
        };

        fetchOrderDetails();
    }, [orderId]);

    const handleBack = () => {
        navigate('/admin/dashboard');
    };

    if (!order) {
        return <div className="od-container"><p>Loading...</p></div>;
    }

    return (
        <div className="od-container">
            <div className="od-header">
                <h2 className="od-title">Chi Tiết Đơn Hàng #{order.id}</h2>
                <button className="od-back-btn" onClick={handleBack}>
                    Quay Lại
                </button>
            </div>

            <div className="od-content">
                {error && <p className="od-error">{error}</p>}
                <div>
                    <h3>Thông Tin Đơn Hàng</h3>
                    <p><strong>Khách Hàng:</strong> {order.customer ? `${order.customer.firstName} ${order.customer.lastName}` : 'Không có'}</p>
                    <p><strong>Tổng Tiền:</strong> {order.totalPrice.toLocaleString()} đ</p>
                    <p><strong>Ngày Tạo:</strong> {new Date(order.created_at).toLocaleString()}</p>
                    <p><strong>Trạng Thái:</strong> {order.orderStatus ? order.orderStatus.statusName : 'Không có'}</p>
                </div>

                <h3>Các Sản Phẩm Trong Đơn Hàng</h3>
                {orderItems.length > 0 ? (
                    <table className="od-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Sản Phẩm</th>
                                <th>Số Lượng</th>
                                <th>Giá</th>
                                <th>Tổng</th>
                            </tr>
                        </thead>
                        <tbody>
                            {orderItems.map(item => (
                                <tr key={item.id}>
                                    <td>{item.id}</td>
                                    <td>{item.product ? item.product.productName : 'Không có'}</td>
                                    <td>{item.quantity}</td>
                                    <td>{item.price.toLocaleString()} đ</td>
                                    <td>{(item.quantity * item.price).toLocaleString()} đ</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <p className="od-empty">Không có sản phẩm nào trong đơn hàng.</p>
                )}
            </div>
        </div>
    );
};

export default OrderDetails;