import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import '../css/CategoryForm.css';

const CategoryForm = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const staffId = '123e4567-e89b-12d3-a456-426614174000';

    const editingCategory = location.state?.category || null;

    const [formData, setFormData] = useState({
        categoryName: editingCategory?.categoryName || '',
        categoryDescription: editingCategory?.categoryDescription || '',
        parentId: editingCategory?.parent?.id || '',
    });
    const [categories, setCategories] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        axios.get('https://rubilia.store/api/categories')
            .then(response => {
                setCategories(response.data);
            })
            .catch(err => {
                console.error('Lỗi lấy danh sách danh mục:', err);
                setError('Không thể lấy danh sách danh mục.');
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
            const selectedParent = categories.find(c => c.id === formData.parentId);
            const currentTime = new Date().toISOString();
            const categoryData = {
                categoryName: formData.categoryName,
                categoryDescription: formData.categoryDescription,
                parent: selectedParent ? { id: selectedParent.id } : null,
                createdAt: currentTime,
                updatedAt: currentTime,
            };

            if (editingCategory) {
                const { createdAt, ...updateData } = categoryData;
                await axios.put(`https://rubilia.store/api/categories/${editingCategory.id}?staffId=${staffId}`, updateData);
                alert('Sửa danh mục thành công!');
            } else {
                await axios.post(`https://rubilia.store/api/categories?staffId=${staffId}`, categoryData);
                alert('Thêm danh mục thành công!');
            }

            navigate('/admin/dashboard');
        } catch (err) {
            console.error('Lỗi xử lý danh mục:', err.response?.data || err.message);
            const errorMessage = err.response?.data?.error || err.response?.data?.message || err.message || 'Lỗi khi xử lý danh mục.';
            setError(errorMessage);
        }
    };

    const handleCancel = () => {
        navigate('/admin/dashboard');
    };

    return (
        <div className="cf-container">
            <div className="cf-header">
                <h2 className="cf-title">
                    {editingCategory ? 'Sửa Danh Mục' : 'Thêm Danh Mục'}
                </h2>
            </div>

            <form onSubmit={handleSubmit} className="cf-form">
                <div className="cf-form-group">
                    <label className="cf-label">Tên Danh Mục</label>
                    <input
                        type="text"
                        name="categoryName"
                        value={formData.categoryName}
                        onChange={handleChange}
                        className="cf-input"
                        required
                    />
                </div>
                <div className="cf-form-group">
                    <label className="cf-label">Mô Tả Danh Mục</label>
                    <textarea
                        name="categoryDescription"
                        value={formData.categoryDescription}
                        onChange={handleChange}
                        className="cf-textarea"
                        rows="5"
                    />
                </div>
                <div className="cf-form-group">
                    <label className="cf-label">Danh Mục Cha</label>
                    <select
                        name="parentId"
                        value={formData.parentId}
                        onChange={handleChange}
                        className="cf-select"
                    >
                        <option value="">Không có</option>
                        {categories.map(category => (
                            <option key={category.id} value={category.id}>
                                {category.categoryName}
                            </option>
                        ))}
                    </select>
                </div>
                {error && (
                    <p className="cf-error">{error}</p>
                )}
                <div className="cf-actions">
                    <button
                        type="submit"
                        className="cf-submit-btn"
                    >
                        {editingCategory ? 'Cập Nhật' : 'Thêm'}
                    </button>
                    <button
                        type="button"
                        className="cf-cancel-btn"
                        onClick={handleCancel}
                    >
                        Hủy
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CategoryForm;