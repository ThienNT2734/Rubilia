import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const UserProfile = () => {
    const navigate = useNavigate();
    const customer = JSON.parse(localStorage.getItem('customer')) || null;

    const [userInfo, setUserInfo] = useState({
        name: customer ? `${customer.firstName} ${customer.lastName}` : '',
        email: customer ? customer.email : '',
        phone: '',
        address: '',
    });
    const [passwordData, setPasswordData] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
    });
    const [error, setError] = useState('');
    const [isOAuth2, setIsOAuth2] = useState(customer?.isOAuth2 || false); // Lấy từ localStorage nếu có

    useEffect(() => {
        // Kiểm tra trạng thái đăng nhập
        console.log('Customer in localStorage:', customer);
        if (!customer || !customer.id) {
            alert('Vui lòng đăng nhập để xem thông tin tài khoản!');
            navigate('/');
            return;
        }

        // Kiểm tra xem tài khoản có phải là OAuth2 không
        axios.get(`http://localhost:8080/api/customers/${customer.id}/is-oauth2`)
            .then(response => {
                setIsOAuth2(response.data.isOAuth2);
                // Cập nhật localStorage
                const updatedCustomer = { ...customer, isOAuth2: response.data.isOAuth2 };
                localStorage.setItem('customer', JSON.stringify(updatedCustomer));
            })
            .catch(err => {
                console.error('Lỗi kiểm tra loại tài khoản:', err.response?.data || err.message);
                setError('Không thể kiểm tra loại tài khoản.');
            });
    }, [customer, navigate]);

    const handleUserInfoChange = (e) => {
        setUserInfo({ ...userInfo, [e.target.name]: e.target.value });
    };

    const handlePasswordChange = (e) => {
        setPasswordData({ ...passwordData, [e.target.name]: e.target.value });
    };

    const handleUserInfoSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            if (!customer || !customer.id) {
                throw new Error('Không tìm thấy thông tin khách hàng. Vui lòng đăng nhập lại.');
            }
            const [firstName, ...lastNameParts] = userInfo.name.split(' ');
            const lastName = lastNameParts.join(' ') || 'Unknown';
            console.log('Dữ liệu gửi lên:', {
                customerId: customer.id,
                first_name: firstName,
                last_name: lastName,
                email: userInfo.email,
                phone_number: userInfo.phone,
                address: userInfo.address
            });
            const response = await axios.put(`http://localhost:8080/api/customers/${customer.id}`, {
                first_name: firstName,
                last_name: lastName,
                email: userInfo.email,
                phone_number: userInfo.phone,
                address: userInfo.address
            });
            alert('Cập nhật thông tin thành công!');
            // Cập nhật localStorage
            const updatedCustomer = { ...customer, firstName: firstName, lastName: lastName };
            localStorage.setItem('customer', JSON.stringify(updatedCustomer));
        } catch (err) {
            console.error('Lỗi cập nhật thông tin:', err.response?.data || err.message);
            const errorMessage = err.response?.data?.error || 
                               err.response?.data?.message || 
                               err.message || 
                               `Lỗi khi cập nhật thông tin (Status: ${err.response?.status || 'N/A'})`;
            setError(errorMessage);
        }
    };

    const handlePasswordSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            if (!customer || !customer.id) {
                throw new Error('Không tìm thấy thông tin khách hàng. Vui lòng đăng nhập lại.');
            }
            console.log('Dữ liệu gửi lên đổi mật khẩu:', {
                customerId: customer.id,
                currentPassword: passwordData.currentPassword,
                newPassword: passwordData.newPassword,
                confirmPassword: passwordData.confirmPassword
            });
            const response = await axios.put(`http://localhost:8080/api/customers/${customer.id}/password`, {
                currentPassword: isOAuth2 ? '' : passwordData.currentPassword, // Không gửi currentPassword nếu là OAuth2
                newPassword: passwordData.newPassword,
                confirmPassword: passwordData.confirmPassword
            });
            alert('Đổi mật khẩu thành công!');
            setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' });
            // Cập nhật localStorage để bỏ cờ isOAuth2 sau khi đổi mật khẩu
            if (isOAuth2) {
                const updatedCustomer = { ...customer, isOAuth2: false };
                localStorage.setItem('customer', JSON.stringify(updatedCustomer));
                setIsOAuth2(false);
            }
        } catch (err) {
            console.error('Lỗi đổi mật khẩu:', err.response?.data || err.message);
            const errorMessage = err.response?.data?.error || 
                               err.response?.data?.message || 
                               err.message || 
                               `Lỗi khi đổi mật khẩu (Status: ${err.response?.status || 'N/A'})`;
            setError(errorMessage);
        }
    };

    if (!customer) {
        return null;
    }

    return (
        <div className="user-profile-container">
            <h2 className="user-profile-title">Thông Tin Người Dùng</h2>

            {/* Thông tin tài khoản hiện tại */}
            <div className="user-profile-section">
                <h3>Thông Tin Tài Khoản</h3>
                <div className="card">
                    <div className="card-body">
                        <p><strong>Email:</strong> {customer.email}</p>
                        <p><strong>Tên:</strong> {customer.firstName}</p>
                        <p><strong>Họ:</strong> {customer.lastName}</p>
                    </div>
                </div>
            </div>

            {/* Cập nhật thông tin cá nhân */}
            <div className="user-profile-section">
                <h3>Cập Nhật Thông Tin Cá Nhân</h3>
                {error && <p className="text-danger">{error}</p>}
                <form onSubmit={handleUserInfoSubmit}>
                    <div className="form-group">
                        <label htmlFor="name">Họ và Tên</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={userInfo.name}
                            onChange={handleUserInfoChange}
                            placeholder="Nhập họ và tên"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="email">Email</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            value={userInfo.email}
                            onChange={handleUserInfoChange}
                            placeholder="Nhập email"
                            required
                            disabled
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="phone">Số Điện Thoại</label>
                        <input
                            type="tel"
                            id="phone"
                            name="phone"
                            value={userInfo.phone}
                            onChange={handleUserInfoChange}
                            placeholder="Nhập số điện thoại"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="address">Địa Chỉ</label>
                        <input
                            type="text"
                            id="address"
                            name="address"
                            value={userInfo.address}
                            onChange={handleUserInfoChange}
                            placeholder="Nhập địa chỉ"
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary">Cập Nhật</button>
                </form>
            </div>

            {/* Đổi mật khẩu */}
            <div className="user-profile-section">
                <h3>Đổi Mật Khẩu</h3>
                {error && <p className="text-danger">{error}</p>}
                <form onSubmit={handlePasswordSubmit}>
                    {!isOAuth2 && (
                        <div className="form-group">
                            <label htmlFor="currentPassword">Mật Khẩu Hiện Tại</label>
                            <input
                                type="password"
                                id="currentPassword"
                                name="currentPassword"
                                value={passwordData.currentPassword}
                                onChange={handlePasswordChange}
                                required
                            />
                        </div>
                    )}
                    <div className="form-group">
                        <label htmlFor="newPassword">Mật Khẩu Mới</label>
                        <input
                            type="password"
                            id="newPassword"
                            name="newPassword"
                            value={passwordData.newPassword}
                            onChange={handlePasswordChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="confirmPassword">Xác Nhận Mật Khẩu</label>
                        <input
                            type="password"
                            id="confirmPassword"
                            name="confirmPassword"
                            value={passwordData.confirmPassword}
                            onChange={handlePasswordChange}
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary">Đổi Mật Khẩu</button>
                </form>
            </div>
        </div>
    );
};

export default UserProfile;