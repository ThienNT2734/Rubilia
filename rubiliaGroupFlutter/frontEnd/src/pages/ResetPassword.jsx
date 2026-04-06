import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios from 'axios';

const ResetPassword = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        newPassword: '',
        confirmPassword: ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        const token = searchParams.get('token');
        if (!token) {
            setError('Token không hợp lệ. Vui lòng thử lại.');
        }
    }, [searchParams]);

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        const token = searchParams.get('token');
        if (!token) {
            setError('Token không hợp lệ.');
            return;
        }

        try {
            const response = await axios.post('https://rubilia.store/api/customers/reset-password', {
                token,
                newPassword: formData.newPassword,
                confirmPassword: formData.confirmPassword
            });
            setSuccess(response.data);
            setTimeout(() => navigate('/'), 3000); // Chuyển về trang chủ sau 3 giây
        } catch (err) {
            console.error('Lỗi đặt lại mật khẩu:', err.response?.data || err.message);
            const errorMessage = err.response?.data || err.message || 'Đã có lỗi xảy ra. Vui lòng thử lại.';
            setError(errorMessage);
        }
    };

    return (
        <div className="container mt-5">
            <h2 className="text-center mb-4">Đặt lại mật khẩu</h2>
            {error && <p className="text-danger text-center">{error}</p>}
            {success && <p className="text-success text-center">{success} Bạn sẽ được chuyển về trang chủ sau vài giây...</p>}
            {!error && !success && (
                <form onSubmit={handleSubmit} className="mx-auto" style={{ maxWidth: '400px' }}>
                    <div className="form-group mb-3">
                        <label htmlFor="newPassword">Mật khẩu mới</label>
                        <input
                            type="password"
                            id="newPassword"
                            name="newPassword"
                            value={formData.newPassword}
                            onChange={handleInputChange}
                            className="form-control"
                            required
                        />
                    </div>
                    <div className="form-group mb-3">
                        <label htmlFor="confirmPassword">Xác nhận mật khẩu</label>
                        <input
                            type="password"
                            id="confirmPassword"
                            name="confirmPassword"
                            value={formData.confirmPassword}
                            onChange={handleInputChange}
                            className="form-control"
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary w-100">Đặt lại mật khẩu</button>
                </form>
            )}
        </div>
    );
};

export default ResetPassword;