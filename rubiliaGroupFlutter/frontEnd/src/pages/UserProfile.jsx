import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../utils/axiosConfig';

const UserProfile = () => {
    const navigate = useNavigate();
    const customer = JSON.parse(localStorage.getItem('customer')) || null;

    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!customer || !customer.id) {
            alert('Vui lòng đăng nhập để xem lịch sử đơn hàng!');
            navigate('/');
            return;
        }

        // Lấy lịch sử đơn hàng của khách hàng
        api.get(`/orders/customer/${customer.id}`)
            .then(response => {
                setOrders(response.data);
                setLoading(false);
            })
            .catch(err => {
                console.error('Lỗi tải lịch sử đơn hàng:', err.response?.data || err.message);
                setError('Không thể tải lịch sử đơn hàng. Vui lòng thử lại sau.');
                setLoading(false);
            });
    }, [customer, navigate]);

    // Format tiền tệ VND
    const formatPrice = (price) => {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
    };

    // Format ngày tháng
    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleString('vi-VN');
    };

    // Lấy trạng thái đơn hàng text
    const getOrderStatusText = (status) => {
        const statusMap = {
            'PENDING': 'Chờ xác nhận',
            'PAID': 'Đã thanh toán',
            'CONFIRMED': 'Đã xác nhận',
            'SHIPPING': 'Đang giao hàng',
            'DELIVERED': 'Đã giao hàng',
            'CANCELLED': 'Đã hủy'
        };
        return statusMap[status] || status;
    };

    // Lấy class màu cho trạng thái
    const getStatusClass = (status) => {
        const classMap = {
            'PENDING': 'warning',
            'PAID': 'info',
            'CONFIRMED': 'primary',
            'SHIPPING': 'info',
            'DELIVERED': 'success',
            'CANCELLED': 'danger'
        };
        return classMap[status] || 'secondary';
    };

    if (!customer) {
        return null;
    }

    if (loading) {
        return (
            <div className="container mt-5 text-center">
                <div className="spinner-border text-primary" role="status"></div>
                <p>Đang tải lịch sử đơn hàng...</p>
            </div>
        );
    }

    return (
        <div className="container mt-5 mb-5">
            <h2 className="mb-4">Lịch Sử Đơn Hàng</h2>

            {/* Thông tin tài khoản */}
            <div className="card mb-4">
                <div className="card-body">
                    <h5 className="card-title">Thông tin tài khoản</h5>
                    <p className="mb-1"><strong>Email:</strong> {customer.email}</p>
                    <p className="mb-0"><strong>Tên khách hàng:</strong> {customer.firstName} {customer.lastName}</p>
                </div>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            {orders.length === 0 ? (
                <div className="card">
                    <div className="card-body text-center py-5">
                        <h5 className="text-muted">Bạn chưa có đơn hàng nào</h5>
                        <p className="text-muted mb-4">Hãy bắt đầu mua sắm để xem lịch sử đơn hàng tại đây</p>
                        <Link to="/all-products" className="btn btn-primary">Mua Sắm Ngay</Link>
                    </div>
                </div>
            ) : (
                <div className="card">
                    <div className="card-body p-0">
                        <div className="table-responsive">
                            <table className="table table-hover mb-0">
                                <thead className="table-light">
                                    <tr>
                                        <th>Mã đơn hàng</th>
                                        <th>Ngày đặt</th>
                                        <th>Tổng tiền</th>
                                        <th>Trạng thái</th>
                                        <th>Chi tiết</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {orders.map(order => (
                                        <tr key={order.id}>
                                            <td><strong>{order.orderNumber || order.id}</strong></td>
                                            <td>{order.created_at ? formatDate(order.created_at) : 'N/A'}</td>
                                            <td><strong>{order.totalPrice ? formatPrice(order.totalPrice) : 'Chưa cập nhật'}</strong></td>
                                            <td>
                                                <span className={`badge bg-${order.paymentStatus === 'PAID' ? 'success' : order.paymentStatus === 'CANCELLED' ? 'danger' : 'warning'}`}>
                                                    {order.paymentStatus || 'CHỜ XÁC NHẬN'}
                                                </span>
                                            </td>
                                            <td>
                                                <Link to={`/admin/order-details/${order.id}`} className="btn btn-sm btn-outline-primary">
                                                    Xem chi tiết
                                                </Link>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default UserProfile;