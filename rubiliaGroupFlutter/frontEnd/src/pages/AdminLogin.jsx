import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { login } from '../utils/auth';

const AdminLogin = () => {
    const [formData, setFormData] = useState({
        user_name: '',
        password_hash: '',
    });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const response = await axios.post('http://localhost:8080/api/staff/login', formData);
            login(response.data); // Lưu trạng thái đăng nhập
            navigate('/admin/dashboard');
        } catch (err) {
            console.error('Lỗi đăng nhập:', err.response?.data || err.message);
            const errorMessage = err.response?.data || 'Lỗi khi đăng nhập. Vui lòng thử lại.';
            setError(errorMessage);
        }
    };

    return (
        <div className="container my-5">
            <div className="row justify-content-center">
                <div className="col-md-6 col-lg-4">
                    <div className="card shadow-sm p-4">
                        <h2 className="text-center mb-4">Đăng Nhập Admin</h2>
                        {error && <div className="alert alert-danger">{error}</div>}
                        <form onSubmit={handleSubmit}>
                            <div className="form-group mb-3">
                                <label htmlFor="user_name" className="form-label">Tên đăng nhập</label>
                                <input
                                    type="text"
                                    id="user_name"
                                    name="user_name"
                                    value={formData.user_name}
                                    onChange={handleChange}
                                    className="form-control"
                                    placeholder="Nhập tên đăng nhập"
                                    required
                                />
                            </div>
                            <div className="form-group mb-3">
                                <label htmlFor="password_hash" className="form-label">Mật khẩu</label>
                                <input
                                    type="password"
                                    id="password_hash"
                                    name="password_hash"
                                    value={formData.password_hash}
                                    onChange={handleChange}
                                    className="form-control"
                                    placeholder="Nhập mật khẩu"
                                    required
                                />
                            </div>
                            <button type="submit" className="btn btn-primary w-100">
                                Đăng Nhập
                            </button>
                        </form>
                        <div className="text-center mt-3">
                            <p>Chưa có tài khoản? <Link to="/admin/register">Đăng ký ngay</Link></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminLogin;