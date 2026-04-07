import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../utils/axiosConfig';
import { adaptProductDataForUpdate } from '../utils/ProductDataAdapter';
import { getCurrentStaff } from '../utils/auth';

const PromotionsPage = () => {
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filter, setFilter] = useState('all'); 
    const [editingProduct, setEditingProduct] = useState(null);
    const [promotionData, setPromotionData] = useState({
        discountPercentage: '',
        promotionStart: '',
        promotionEnd: ''
    });

    useEffect(() => {
        fetchAllProducts();
    }, []);

    useEffect(() => {
        filterProducts();
    }, [products, filter]);

    const fetchAllProducts = async () => {
        try {
            const response = await api.get('/products');
            setProducts(response.data);
        } catch (err) {
            setError('Không thể tải danh sách sản phẩm');
        } finally {
            setLoading(false);
        }
    };

    const filterProducts = () => {
        let filtered = products;
        if (filter === 'promotional') {
            filtered = products.filter(product =>
                product.isOnPromotion && product.discountPercentage > 0
            );
        } else if (filter === 'non-promotional') {
            filtered = products.filter(product =>
                !product.isOnPromotion || product.discountPercentage === 0
            );
        }
        setFilteredProducts(filtered);
    };

    const callUpdateApi = async (productId, updatedProduct) => {
        try {
            const staff = getCurrentStaff();
            if (!staff?.id) {
                setError('Vui lòng đăng nhập lại với quyền quản trị.');
                return;
            }

            await api.put(
                `/products/${productId}?staffId=${staff.id}`, 
                updatedProduct,
                { withCredentials: true }
            );

            fetchAllProducts();
            setEditingProduct(null);
            alert('Cập nhật khuyến mãi thành công!');
            setError('');
        } catch (err) {
            console.error("Lỗi cập nhật:", err.response?.data);
            setError(err.response?.data?.message || 'Lỗi phân quyền hoặc lỗi Server');
        }
    };

    const handleTogglePromotion = async (productId, currentStatus) => {
        const product = products.find(p => p.id === productId);
        if (!product) return;

        const updatedProduct = adaptProductDataForUpdate(product, {
            isOnPromotion: !currentStatus
        });
        await callUpdateApi(productId, updatedProduct);
    };

    const handleSetPromotion = async (productId, discountPercentage, startDate, endDate) => {
        const product = products.find(p => p.id === productId);
        if (!product) return;

        const updatedProduct = adaptProductDataForUpdate(product, {
            discountPercentage: parseFloat(discountPercentage) || 0,
            promotionStart: startDate ? new Date(startDate).toISOString() : null,
            promotionEnd: endDate ? new Date(endDate).toISOString() : null,
            isOnPromotion: parseFloat(discountPercentage) > 0
        });

        await callUpdateApi(productId, updatedProduct);
    };

    const handleEditPromotion = (product) => {
        setEditingProduct(product.id);
        setPromotionData({
            discountPercentage: product.discountPercentage || '',
            promotionStart: product.promotionStart ? new Date(product.promotionStart).toISOString().slice(0, 16) : '',
            promotionEnd: product.promotionEnd ? new Date(product.promotionEnd).toISOString().slice(0, 16) : ''
        });
    };

    const handleCancelEdit = () => {
        setEditingProduct(null);
        setPromotionData({ discountPercentage: '', promotionStart: '', promotionEnd: '' });
    };

    const handleInputChange = (field, value) => {
        setPromotionData(prev => ({ ...prev, [field]: value }));
    };

    if (loading) {
        return (
            <div className="container mt-5 text-center">
                <div className="spinner-border text-primary" role="status"></div>
                <p>Đang tải dữ liệu...</p>
            </div>
        );
    }

    return (
        <div className="container mt-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2 className="fw-bold">Quản Lý Khuyến Mãi</h2>
                <Link to="/admin/product/new" className="btn btn-primary shadow-sm">
                    <i className="fas fa-plus"></i> Thêm Sản Phẩm Mới
                </Link>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            <div className="mb-4">
                <div className="btn-group shadow-sm">
                    <button className={`btn ${filter === 'all' ? 'btn-primary' : 'btn-outline-primary'}`} onClick={() => setFilter('all')}>
                        Tất Cả ({products.length})
                    </button>
                    <button className={`btn ${filter === 'promotional' ? 'btn-success' : 'btn-outline-success'}`} onClick={() => setFilter('promotional')}>
                        Đang Khuyến Mãi
                    </button>
                    <button className={`btn ${filter === 'non-promotional' ? 'btn-secondary' : 'btn-outline-secondary'}`} onClick={() => setFilter('non-promotional')}>
                        Chưa Khuyến Mãi
                    </button>
                </div>
            </div>

            <div className="card border-0 shadow-sm">
                <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                        <thead className="table-light">
                            <tr>
                                <th>Hình Ảnh</th>
                                <th>Tên Sản Phẩm</th>
                                <th>Giá Gốc</th>
                                <th style={{ width: '120px' }}>Giảm (%)</th>
                                <th>Giá Sau Giảm</th>
                                <th>Thời Gian</th>
                                <th className="text-center">Hành Động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredProducts.map(product => {
                                const imageUrl = product.galleries?.[0]?.image
                                    ? `https://rubilia.store${product.galleries[0].image}`
                                    : 'https://via.placeholder.com/50';
                                    
                                // Logic tính giá gốc và giá khuyến mãi
                                const originalPrice = product.comparePrice || product.price || 0;
                                const discountPercent = (product.isOnPromotion && product.discountPercentage) ? product.discountPercentage : 0;
                                const finalPrice = Math.round(originalPrice * (1 - discountPercent / 100));

                                return (
                                    <tr key={product.id}>
                                        <td>
                                            <img src={imageUrl} alt="" className="rounded shadow-sm" style={{ width: '50px', height: '50px', objectFit: 'cover' }} />
                                        </td>
                                        <td className="fw-bold" style={{ maxWidth: '200px' }}>{product.productName}</td>
                                        <td>
                                            {discountPercent > 0 ? (
                                                <del className="text-muted small">{originalPrice.toLocaleString()} đ</del>
                                            ) : (
                                                <span className="fw-bold text-dark">{originalPrice.toLocaleString()} đ</span>
                                            )}
                                        </td>
                                        <td>
                                            {editingProduct === product.id ? (
                                                <input type="number" className="form-control form-control-sm" value={promotionData.discountPercentage} onChange={(e) => handleInputChange('discountPercentage', e.target.value)} />
                                            ) : (
                                                discountPercent > 0 ? <span className="badge bg-danger">-{discountPercent}%</span> : <span className="text-muted">-</span>
                                            )}
                                        </td>
                                        <td>
                                            {editingProduct === product.id ? (
                                                <span className="text-danger fw-bold">
                                                    {Math.round(originalPrice * (1 - (parseFloat(promotionData.discountPercentage) || 0) / 100)).toLocaleString()} đ
                                                </span>
                                            ) : (
                                                discountPercent > 0 ? (
                                                    <span className="text-danger fw-bold">{finalPrice.toLocaleString()} đ</span>
                                                ) : <span className="text-muted">-</span>
                                            )}
                                        </td>
                                        <td style={{ fontSize: '0.85rem' }}>
                                            {editingProduct === product.id ? (
                                                <div>
                                                    <input type="datetime-local" className="form-control form-control-sm mb-1" value={promotionData.promotionStart} onChange={(e) => handleInputChange('promotionStart', e.target.value)} />
                                                    <input type="datetime-local" className="form-control form-control-sm" value={promotionData.promotionEnd} onChange={(e) => handleInputChange('promotionEnd', e.target.value)} />
                                                </div>
                                            ) : (
                                                product.isOnPromotion ? (
                                                    <div>
                                                        <div>S: {product.promotionStart ? new Date(product.promotionStart).toLocaleString('vi-VN') : 'N/A'}</div>
                                                        <div>E: {product.promotionEnd ? new Date(product.promotionEnd).toLocaleString('vi-VN') : 'N/A'}</div>
                                                    </div>
                                                ) : '-'
                                            )}
                                        </td>
                                        <td className="text-center">
                                            {editingProduct === product.id ? (
                                                <div className="btn-group">
                                                    <button className="btn btn-sm btn-success" onClick={() => handleSetPromotion(product.id, promotionData.discountPercentage, promotionData.promotionStart, promotionData.promotionEnd)}>Lưu</button>
                                                    <button className="btn btn-sm btn-secondary" onClick={handleCancelEdit}>Hủy</button>
                                                </div>
                                            ) : (
                                                <div className="btn-group">
                                                    <button className="btn btn-sm btn-outline-primary" onClick={() => handleEditPromotion(product)}>Thiết Lập</button>
                                                    {product.isOnPromotion && (
                                                        <button className="btn btn-sm btn-outline-danger" onClick={() => handleTogglePromotion(product.id, product.isOnPromotion)}>Tắt KM</button>
                                                    )}
                                                </div>
                                            )}
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default PromotionsPage;