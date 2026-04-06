import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';

const SaleForm = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const staffId = '123e4567-e89b-12d3-a456-426614174000';

    const editingSale = location.state?.sale || null;

    const [formData, setFormData] = useState({
        name: editingSale?.name || '',
        discountPercentage: editingSale?.discountPercentage || 0,
        startDate: editingSale?.startDate ? new Date(editingSale.startDate).toISOString().slice(0, 16) : '',
        endDate: editingSale?.endDate ? new Date(editingSale.endDate).toISOString().slice(0, 16) : '',
        productId: editingSale?.product?.id || '',
    });
    const [products, setProducts] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        axios.get('https://rubilia.store/api/products')
            .then(response => {
                setProducts(response.data);
            })
            .catch(err => {
                console.error('Lỗi lấy danh sách sản phẩm:', err);
                setError('Không thể lấy danh sách sản phẩm.');
            });
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const selectedProduct = products.find(p => p.id === formData.productId);
            const saleData = {
                name: formData.name,
                discountPercentage: parseFloat(formData.discountPercentage),
                startDate: new Date(formData.startDate).toISOString(),
                endDate: new Date(formData.endDate).toISOString(),
                product: selectedProduct ? { id: selectedProduct.id } : null,
            };

            if (editingSale) {
                await axios.put(`https://rubilia.store/api/sales/${editingSale.id}?staffId=${staffId}`, saleData);
                alert('Sửa Flash Sale thành công!');
            } else {
                await axios.post(`https://rubilia.store/api/sales?staffId=${staffId}`, saleData);
                alert('Thêm Flash Sale thành công!');
            }

            navigate('/admin/dashboard');
        } catch (err) {
            console.error('Lỗi xử lý Flash Sale:', err.response?.data || err.message);
            const errorMessage = err.response?.data?.error || err.response?.data?.message || err.message || 'Lỗi khi xử lý Flash Sale.';
            setError(errorMessage);
        }
    };

    const handleCancel = () => {
        navigate('/admin/dashboard');
    };

    return (
        <div className="admin-dashboard-container">
            <div className="admin-dashboard-header">
                <h2 className="admin-dashboard-title">{editingSale ? 'Sửa Flash Sale' : 'Thêm Flash Sale'}</h2>
            </div>

            <div className="admin-dashboard-products-tab">
                <form onSubmit={handleSubmit}>
                    <div className="admin-dashboard-form-group">
                        <label className="admin-dashboard-form-label">Tên Flash Sale</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            className="admin-dashboard-form-control"
                            required
                        />
                    </div>
                    <div className="admin-dashboard-form-group">
                        <label className="admin-dashboard-form-label">Phần Trăm Giảm Giá (%)</label>
                        <input
                            type="number"
                            name="discountPercentage"
                            value={formData.discountPercentage}
                            onChange={handleChange}
                            className="admin-dashboard-form-control"
                            min="0"
                            max="100"
                            required
                        />
                    </div>
                    <div className="admin-dashboard-form-group">
                        <label className="admin-dashboard-form-label">Thời Gian Bắt Đầu</label>
                        <input
                            type="datetime-local"
                            name="startDate"
                            value={formData.startDate}
                            onChange={handleChange}
                            className="admin-dashboard-form-control"
                            required
                        />
                    </div>
                    <div className="admin-dashboard-form-group">
                        <label className="admin-dashboard-form-label">Thời Gian Kết Thúc</label>
                        <input
                            type="datetime-local"
                            name="endDate"
                            value={formData.endDate}
                            onChange={handleChange}
                            className="admin-dashboard-form-control"
                            required
                        />
                    </div>
                    <div className="admin-dashboard-form-group">
                        <label className="admin-dashboard-form-label">Sản Phẩm</label>
                        <select
                            name="productId"
                            value={formData.productId}
                            onChange={handleChange}
                            className="admin-dashboard-form-control"
                            required
                        >
                            <option value="">Chọn sản phẩm</option>
                            {products.map(product => (
                                <option key={product.id} value={product.id}>
                                    {product.productName}
                                </option>
                            ))}
                        </select>
                    </div>
                    {error && <p className="admin-dashboard-error">{error}</p>}
                    <div className="admin-dashboard-actions">
                        <button type="submit" className="admin-dashboard-add-btn">
                            {editingSale ? 'Cập Nhật' : 'Thêm'}
                        </button>
                        <button
                            type="button"
                            className="admin-dashboard-logout-btn"
                            onClick={handleCancel}
                        >
                            Hủy
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default SaleForm;