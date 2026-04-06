import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';

const AdminRegister = () => {
    const [formData, setFormData] = useState({
        user_name: '',
        password_hash: '',
        email: '',
        name: '',
    });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    console.log('Rendering AdminRegister component');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const response = await axios.post('https://rubilia.store/api/staff/register', formData);
            alert('Đăng ký thành công! Vui lòng đăng nhập.');
            navigate('/admin/login');
        } catch (err) {
            console.error('Lỗi đăng ký:', err.response?.data || err.message);
            const errorMessage = err.response?.data?.error || err.response?.data?.message || err.response?.data || 'Lỗi khi đăng ký tài khoản.';
            setError(errorMessage);
        }
    };

    return (
        <div className="container my-5">
            <div className="row justify-content-center">
                <div className="col-md-6 col-lg-4">
                    <div className="card shadow-sm p-4">
                        <h2 className="text-center mb-4">Đăng Ký Tài Khoản Admin</h2>
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
                            <div className="form-group mb-3">
                                <label htmlFor="email" className="form-label">Email</label>
                                <input
                                    type="email"
                                    id="email"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    className="form-control"
                                    placeholder="Nhập email"
                                    required
                                />
                            </div>
                            <div className="form-group mb-3">
                                <label htmlFor="name" className="form-label">Họ và tên</label>
                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    value={formData.name}
                                    onChange={handleChange}
                                    className="form-control"
                                    placeholder="Nhập họ và tên"
                                    required
                                />
                            </div>
                            <button type="submit" className="btn btn-primary w-100">
                                Đăng Ký
                            </button>
                        </form>
                        <div className="text-center mt-3">
                            <p>Đã có tài khoản? <Link to="/admin/login">Đăng nhập</Link></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminRegister;