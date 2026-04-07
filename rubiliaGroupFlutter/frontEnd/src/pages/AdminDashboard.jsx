import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../utils/axiosConfig';
import { logout, isAuthenticated, getCurrentStaff } from '../utils/auth';
import { adaptProductDataForUpdate } from '../utils/ProductDataAdapter';
import '../css/AdminDashboard.css';

const AdminDashboard = () => {
    const [activeTab, setActiveTab] = useState('products');
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [sales, setSales] = useState([]);
    const [orders, setOrders] = useState([]);
    const [reviewPosts, setReviewPosts] = useState([]);
    const [displayAreas, setDisplayAreas] = useState([]);
    const [toast, setToast] = useState({ message: '', type: '', visible: false });
    const navigate = useNavigate();

    const displayAreasOptions = [
        { name: 'flashsale', label: 'Flash Sale' },
        { name: 'trang_diem', label: 'Trang Điểm' },
        { name: 'cham_soc_da', label: 'Chăm Sóc Da' },
        { name: 'cham_soc_co_the', label: 'Chăm Sóc Cơ Thể' },
        { name: 'phu_kien', label: 'Phụ Kiện' },
        { name: 'mat_na', label: 'Mặt Nạ' },
        { name: 'deal_khung_chao_he', label: 'Deal Khủng Chào Hè' },
        { name: 'highlighted', label: 'Sản Phẩm Nổi Bật' },
    ];

    const [currentPage, setCurrentPage] = useState({});
    const [searchQuery, setSearchQuery] = useState('');
    const productsPerPage = 5;

    useEffect(() => {
        if (!isAuthenticated()) {
            navigate('/admin/login');
        }
    }, [navigate]);

    const showToast = (message, type) => {
        setToast({ message, type, visible: true });
        setTimeout(() => setToast({ message: '', type: '', visible: false }), 3000);
    };

    const parseCreatedAt = (createdAt) => {
        if (!createdAt) return null;
        if (Array.isArray(createdAt) && createdAt.length >= 7) {
            const [year, month, day, hour, minute, second, nano] = createdAt;
            return new Date(year, month - 1, day, hour, minute, second, Math.floor(nano / 1000000));
        }
        if (typeof createdAt === 'string') {
            return new Date(createdAt);
        }
        return null;
    };

    const fetchOrders = async () => {
        try {
            const response = await api.get('/orders', {
                headers: { 'Accept': 'application/json' },
            });
            setOrders(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            console.error('Lỗi lấy danh sách đơn hàng:', err.response?.data || err.message);
            showToast('Không thể lấy danh sách đơn hàng: ' + (err.response?.data?.message || err.message), 'error');
            setOrders([]);
        }
    };

    const fetchProducts = async () => {
        try {
            const response = await api.get('/products', {
                headers: { 'Accept': 'application/json' },
            });
            setProducts(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            console.error('Lỗi lấy danh sách sản phẩm:', err.response?.data || err.message);
            showToast('Không thể lấy danh sách sản phẩm: ' + (err.response?.data?.message || err.message), 'error');
            setProducts([]);
        }
    };

    const fetchCategories = async () => {
        try {
            const response = await api.get('/categories', {
                headers: { 'Accept': 'application/json' },
            });
            setCategories(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            console.error('Lỗi lấy danh sách danh mục:', err.response?.data || err.message);
            showToast('Không thể lấy danh sách danh mục: ' + (err.response?.data?.message || err.message), 'error');
            setCategories([]);
        }
    };

    const fetchSales = async () => {
        try {
            const response = await api.get('/sales', {
                headers: { 'Accept': 'application/json' },
            });
            setSales(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            console.error('Lỗi lấy danh sách Flash Sale:', err.response?.data || err.message);
            showToast('Không thể lấy danh sách Flash Sale: ' + (err.response?.data?.message || err.message), 'error');
            setSales([]);
        }
    };

    const fetchReviewPosts = async () => {
        try {
            const response = await api.get('/review-posts', {
                headers: { 'Accept': 'application/json' },
            });
            setReviewPosts(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            console.error('Lỗi lấy danh sách bài viết:', err.response?.data || err.message);
            showToast('Không thể lấy danh sách bài viết: ' + (err.response?.data?.message || err.message), 'error');
            setReviewPosts([]);
        }
    };

    const fetchDisplayAreas = async () => {
        try {
            const areas = displayAreasOptions.map(option => option.name);
            const fetchPromises = areas.map(area =>
                api.get(`/products/display-area/${area}`)
                    .then(response => ({ area, products: response.data }))
                    .catch(error => {
                        console.error(`Error fetching products for ${area}:`, error);
                        return { area, products: [] };
                    })
            );

            const results = await Promise.all(fetchPromises);
            const areaData = results.map(({ area, products }) => ({
                name: area,
                label: displayAreasOptions.find(opt => opt.name === area).label,
                products: Array.isArray(products) ? products : [],
            }));
            setDisplayAreas(areaData);

            const initialPages = {};
            areaData.forEach(area => {
                initialPages[area.name] = 1;
            });
            setCurrentPage(initialPages);
        } catch (err) {
            console.error('Lỗi lấy danh sách khu vực hiển thị:', err);
            showToast('Không thể lấy danh sách khu vực hiển thị.', 'error');
            setDisplayAreas([]);
        }
    };

    useEffect(() => {
        if (!isAuthenticated()) return;

        const controller = new AbortController();
        const fetchData = async () => {
            if (activeTab === 'products' || activeTab === 'inventory') {
                await fetchProducts();
            } else if (activeTab === 'categories') {
                await fetchCategories();
            } else if (activeTab === 'flash-sale') {
                await fetchSales();
            } else if (activeTab === 'orders') {
                await fetchOrders();
            } else if (activeTab === 'review-posts') {
                await fetchReviewPosts();
            } else if (activeTab === 'display-areas') {
                await fetchDisplayAreas();
            }
        };

        fetchData();
        return () => controller.abort();
    }, [activeTab]);

    const handleLogout = () => {
        logout();
        navigate('/admin/login');
    };

    const handleOpenProductForm = (product = null) => {
        navigate('/admin/product-form', { state: { product, editMode: !!product } });
    };

    const handleDeleteProduct = async (productId) => {
        if (window.confirm('Bạn có chắc muốn xóa sản phẩm này?')) {
            try {
                await api.delete(`/products/${productId}`, { withCredentials: true });
                showToast('Xóa sản phẩm thành công!', 'success');
                setProducts(products.filter(p => p.id !== productId));
            } catch (err) {
                console.error('Lỗi xóa sản phẩm:', err.response?.data || err.message);
                showToast(err.response?.data?.message || 'Lỗi khi xóa sản phẩm.', 'error');
            }
        }
    };

    const handleOpenCategoryForm = (category = null) => {
        navigate('/admin/category-form', { state: { category } });
    };

    const handleDeleteCategory = async (categoryId) => {
        if (window.confirm('Bạn có chắc muốn xóa danh mục này?')) {
            try {
                await api.delete(`/categories/${categoryId}`, { withCredentials: true });
                showToast('Xóa danh mục thành công!', 'success');
                setCategories(categories.filter(c => c.id !== categoryId));
            } catch (err) {
                console.error('Lỗi xóa danh mục:', err.response?.data || err.message);
                showToast(err.response?.data?.message || 'Lỗi khi xóa danh mục.', 'error');
            }
        }
    };

    const handleOpenSaleForm = (sale = null) => {
        navigate('/admin/sale-form', { state: { sale } });
    };

    const handleDeleteSale = async (saleId) => {
        if (window.confirm('Bạn có chắc muốn xóa Flash Sale này?')) {
            try {
                await api.delete(`/sales/${saleId}`, { withCredentials: true });
                showToast('Xóa Flash Sale thành công!', 'success');
                setSales(sales.filter(s => s.id !== saleId));
            } catch (err) {
                console.error('Lỗi xóa Flash Sale:', err.response?.data || err.message);
                showToast(err.response?.data?.message || 'Lỗi khi xóa Flash Sale.', 'error');
            }
        }
    };

    const handleUpdateInventory = async (productId, newQuantity) => {
        try {
            const parsedQuantity = parseInt(newQuantity);
            if (isNaN(parsedQuantity) || parsedQuantity < 0) {
                throw new Error('Số lượng không hợp lệ');
            }

            const product = products.find(p => p.id === productId);
            const staff = getCurrentStaff();
            if (!staff?.id) {
                throw new Error('Không tìm thấy thông tin nhân viên. Vui lòng đăng nhập lại.');
            }

            const updatedProduct = adaptProductDataForUpdate(product, { quantity: parsedQuantity });
            await api.put(`/products/${productId}?staffId=${staff.id}`, updatedProduct, {
                withCredentials: true
            });
            showToast('Cập nhật tồn kho thành công!', 'success');
            setProducts(products.map(p => p.id === productId ? { ...p, quantity: parsedQuantity } : p));
        } catch (err) {
            console.error('Lỗi cập nhật tồn kho:', err.response?.data || err.message);
            showToast(err.response?.data?.message || err.message, 'error');
        }
    };

    const handleUpdateProductDisplay = async (productId, area, selected) => {
        try {
            const product = products.find(p => p.id === productId);
            if (!product) {
                throw new Error('Sản phẩm không tồn tại');
            }

            const staff = getCurrentStaff();
            if (!staff?.id) {
                throw new Error('Không tìm thấy thông tin nhân viên. Vui lòng đăng nhập lại.');
            }

            const currentAreas = Array.isArray(product.displayInfos)
                ? product.displayInfos.map(info => info.displayArea).filter(area => area != null)
                : [];
            const updatedAreas = selected
                ? [...new Set([...currentAreas, area])]
                : currentAreas.filter(a => a !== area);

            const updatedProduct = adaptProductDataForUpdate(product, { displayAreas: updatedAreas });
            await api.put(`/products/${productId}?staffId=${staff.id}`, updatedProduct, {
                withCredentials: true
            });
            showToast('Cập nhật khu vực hiển thị thành công!', 'success');
            fetchDisplayAreas();
            fetchProducts();
        } catch (err) {
            console.error('Lỗi cập nhật khu vực hiển thị:', err.response?.data || err.message);
            showToast(err.response?.data?.message || err.message, 'error');
        }
    };

    const handleUpdateProductStats = async (productId, field, value) => {
        try {
            const product = products.find(p => p.id === productId);
            if (!product) {
                throw new Error('Sản phẩm không tồn tại');
            }

            const staff = getCurrentStaff();
            if (!staff?.id) {
                throw new Error('Không tìm thấy thông tin nhân viên. Vui lòng đăng nhập lại.');
            }

            const currentSalesCount = parseFloat(product.displayInfos?.[0]?.salesCount) || 0;
            let currentRating = parseFloat(product.displayInfos?.[0]?.rating) || 0;
            const currentAreas = Array.isArray(product.displayInfos)
                ? product.displayInfos.map(info => info.displayArea).filter(area => area != null)
                : [];

            let salesCount = currentSalesCount;
            let rating = currentRating;

            if (field === 'salesCount') {
                salesCount = parseFloat(value) || 0;
                if (isNaN(salesCount) || salesCount < 0) {
                    throw new Error('Số lượng bán không hợp lệ');
                }
            } else if (field === 'rating') {
                rating = parseFloat(value);
                if (isNaN(rating) || rating < 1 || rating > 5) {
                    rating = 1;
                }
            }

            const updatedProduct = adaptProductDataForUpdate(product, {
                salesCount,
                rating,
                displayAreas: currentAreas
            });
            await api.put(`/products/${productId}?staffId=${staff.id}`, updatedProduct, {
                withCredentials: true
            });
            showToast('Cập nhật số liệu sản phẩm thành công!', 'success');
            fetchProducts();
        } catch (err) {
            console.error('Lỗi cập nhật số liệu sản phẩm:', err.response?.data || err.message);
            showToast(err.response?.data?.message || err.message, 'error');
        }
    };

    const handleApproveOrder = async (orderId) => {
        try {
            const staff = getCurrentStaff();
            if (!staff?.id) {
                throw new Error('Không tìm thấy thông tin nhân viên. Vui lòng đăng nhập lại.');
            }
            await api.put(`/orders/${orderId}/approve?staffId=${staff.id}`, {}, {
                withCredentials: true
            });
            showToast('Duyệt đơn hàng thành công!', 'success');
            await fetchOrders();
        } catch (err) {
            console.error('Lỗi duyệt đơn hàng:', err.response?.data || err.message);
            showToast(err.response?.data?.message || err.message, 'error');
        }
    };

    const handleShipOrder = async (orderId) => {
        try {
            const staff = getCurrentStaff();
            if (!staff?.id) {
                throw new Error('Không tìm thấy thông tin nhân viên. Vui lòng đăng nhập lại.');
            }
            await api.put(`/orders/${orderId}/ship?staffId=${staff.id}`, {}, {
                withCredentials: true
            });
            showToast('Đánh dấu giao hàng thành công!', 'success');
            await fetchOrders();
        } catch (err) {
            console.error('Lỗi đánh dấu giao hàng:', err.response?.data || err.message);
            showToast(err.response?.data?.message || err.message, 'error');
        }
    };

    const handleOpenReviewPostForm = (reviewPost = null) => {
        navigate('/admin/review-post-form', { state: { post: reviewPost, editMode: !!reviewPost } });
    };

    const handleDeleteReviewPost = async (reviewPostId) => {
        if (window.confirm('Bạn có chắc muốn xóa bài viết này?')) {
            try {
                await api.delete(`/review-posts/${reviewPostId}`, { withCredentials: true });
                showToast('Xóa bài viết thành công!', 'success');
                setReviewPosts(reviewPosts.filter(rp => rp.id !== reviewPostId));
            } catch (err) {
                console.error('Lỗi xóa bài viết:', err.response?.data || err.message);
                showToast(err.response?.data?.message || err.message, 'error');
            }
        }
    };

    const handlePageChange = (area, page) => {
        setCurrentPage(prev => ({ ...prev, [area]: page }));
    };

    const filteredProducts = Array.isArray(products)
        ? products.filter(product =>
            product?.productName?.toLowerCase().includes(searchQuery.toLowerCase())
        )
        : [];

    if (!isAuthenticated()) {
        return null;
    }

    return (
        <div className="admin-dashboard-container">
            {toast.visible && (
                <div className={`admin-dashboard-toast admin-dashboard-toast-${toast.type}`}>
                    {toast.message}
                </div>
            )}
            <div className="admin-dashboard-sidebar">
                <div className="admin-dashboard-header">
                    <h2 className="admin-dashboard-title">Admin Panel</h2>
                </div>
                <div className="admin-dashboard-tabs">
                    <button
                        className={`admin-dashboard-tab-btn ${activeTab === 'products' ? 'active' : ''}`}
                        onClick={() => setActiveTab('products')}
                    >
                        <span className="tab-icon">📦</span> Sản Phẩm
                    </button>
                    <button
                        className={`admin-dashboard-tab-btn ${activeTab === 'categories' ? 'active' : ''}`}
                        onClick={() => setActiveTab('categories')}
                    >
                        <span className="tab-icon">📋</span> Danh Mục
                    </button>
                    <button
                        className={`admin-dashboard-tab-btn ${activeTab === 'inventory' ? 'active' : ''}`}
                        onClick={() => setActiveTab('inventory')}
                    >
                        <span className="tab-icon">🏬</span> Tồn Kho
                    </button>
                    <button
                        className={`admin-dashboard-tab-btn ${activeTab === 'orders' ? 'active' : ''}`}
                        onClick={() => setActiveTab('orders')}
                    >
                        <span className="tab-icon">🛒</span> Đơn Hàng
                    </button>
                    <button
                        className={`admin-dashboard-tab-btn ${activeTab === 'review-posts' ? 'active' : ''}`}
                        onClick={() => setActiveTab('review-posts')}
                    >
                        <span className="tab-icon">📝</span> Bài Viết
                    </button>
                    <button
                        className={`admin-dashboard-tab-btn ${activeTab === 'display-areas' ? 'active' : ''}`}
                        onClick={() => setActiveTab('display-areas')}
                    >
                        <span className="tab-icon">🖼️</span> Khu Vực Hiển Thị
                    </button>
                    <button
                        className={`admin-dashboard-tab-btn ${activeTab === 'promotions' ? 'active' : ''}`}
                        onClick={() => navigate('/admin/promotions')}
                    >
                        <span className="tab-icon">🎉</span> Khuyến Mãi
                    </button>
                    <button
                        className={`admin-dashboard-tab-btn ${activeTab === 'reviews-and-comments' ? 'active' : ''}`}
                        onClick={() => navigate('/admin/dashboard2')}
                    >
                        <span className="tab-icon">⭐</span> Đánh Giá & Bình Luận
                    </button>
                </div>
                <button className="admin-dashboard-logout-btn" onClick={handleLogout}>
                    <span className="tab-icon">🚪</span> Đăng Xuất
                </button>
            </div>
            <div className="admin-dashboard-content">
                {activeTab === 'products' && (
                    <div className="admin-dashboard-tab-content">
                        <h3>Quản Lý Sản Phẩm</h3>
                        <div className="admin-dashboard-actions">
                            <button className="admin-dashboard-add-btn" onClick={() => handleOpenProductForm()}>
                                Thêm Sản Phẩm
                            </button>
                        </div>
                        {products.length > 0 ? (
                            <div className="admin-dashboard-table-container">
                                <table className="admin-dashboard-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Hình Ảnh</th>
                                            <th>Tên Sản Phẩm</th>
                                            <th>Giá</th>
                                            <th>Số Lượng</th>
                                            <th>Số Lượng Bán (k)</th>
                                            <th>Số Sao (1-5)</th>
                                            <th>Hành Động</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {products.map(product => (
                                            <tr key={product.id}>
                                                <td>{product.id}</td>
                                                <td>
                                                    {product.galleries && product.galleries.length > 0 ? (
                                                        <img
                                                            src={`https://rubilia.store${product.galleries[0].image}`}
                                                            alt={product.productName}
                                                            className="product-image"
                                                            onError={(e) => {
                                                                e.target.src = 'https://placehold.co/50x50?text=Image+Not+Found';
                                                                e.target.alt = 'Image not found';
                                                            }}
                                                        />
                                                    ) : (
                                                        <img
                                                            src="https://placehold.co/50x50?text=Image+Not+Found"
                                                            alt="No Image"
                                                            className="product-image"
                                                        />
                                                    )}
                                                </td>
                                                <td>{product.productName}</td>
                                                <td>{(product.price || product.salePrice) ? (product.price || product.salePrice).toLocaleString() : '0'} đ</td>
                                                <td>{product.quantity}</td>
                                                <td>
                                                    <input
                                                        type="number"
                                                        value={product.displayInfos?.[0]?.salesCount || 0}
                                                        onChange={(e) => {
                                                            const updatedProducts = products.map(p =>
                                                                p.id === product.id
                                                                    ? {
                                                                          ...p,
                                                                          displayInfos: p.displayInfos?.length
                                                                              ? [{ ...p.displayInfos[0], salesCount: e.target.value }]
                                                                              : [{ salesCount: e.target.value }],
                                                                      }
                                                                    : p
                                                            );
                                                            setProducts(updatedProducts);
                                                        }}
                                                        onBlur={(e) => handleUpdateProductStats(product.id, 'salesCount', e.target.value)}
                                                        className="admin-dashboard-form-control"
                                                        style={{ width: '100px' }}
                                                        step="0.1"
                                                        min="0"
                                                    />
                                                </td>
                                                <td>
                                                    <input
                                                        type="number"
                                                        value={product.displayInfos?.[0]?.rating || 0}
                                                        onChange={(e) => {
                                                            const updatedProducts = products.map(p =>
                                                                p.id === product.id
                                                                    ? {
                                                                          ...p,
                                                                          displayInfos: p.displayInfos?.length
                                                                              ? [{ ...p.displayInfos[0], rating: e.target.value }]
                                                                              : [{ rating: e.target.value }],
                                                                      }
                                                                    : p
                                                            );
                                                            setProducts(updatedProducts);
                                                        }}
                                                        onBlur={(e) => handleUpdateProductStats(product.id, 'rating', e.target.value)}
                                                        className="admin-dashboard-form-control"
                                                        style={{ width: '100px' }}
                                                        step="0.1"
                                                        min="1"
                                                        max="5"
                                                    />
                                                </td>
                                                <td className="actions">
                                                    <button
                                                        className="edit-btn"
                                                        onClick={() => handleOpenProductForm(product)}
                                                        title="Chỉnh sửa sản phẩm"
                                                    >
                                                        Sửa
                                                    </button>
                                                    <button
                                                        className="delete-btn"
                                                        onClick={() => handleDeleteProduct(product.id)}
                                                        title="Xóa sản phẩm"
                                                    >
                                                        Xóa
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <p className="admin-dashboard-empty">Chưa có sản phẩm nào.</p>
                        )}
                    </div>
                )}

                {activeTab === 'categories' && (
                    <div className="admin-dashboard-tab-content">
                        <h3>Quản Lý Danh Mục</h3>
                        <div className="admin-dashboard-actions">
                            <button className="admin-dashboard-add-btn" onClick={() => handleOpenCategoryForm()}>
                                Thêm Danh Mục
                            </button>
                        </div>
                        {categories.length > 0 ? (
                            <div className="admin-dashboard-table-container">
                                <table className="admin-dashboard-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Tên Danh Mục</th>
                                            <th>Mô Tả</th>
                                            <th>Danh Mục Cha</th>
                                            <th>Hành Động</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {categories.map(category => (
                                            <tr key={category.id}>
                                                <td>{category.id}</td>
                                                <td>{category.categoryName}</td>
                                                <td>{category.categoryDescription || 'Không có'}</td>
                                                <td>{category.parent ? category.parent.categoryName : 'Không có'}</td>
                                                <td className="actions">
                                                    <button
                                                        className="edit-btn"
                                                        onClick={() => handleOpenCategoryForm(category)}
                                                        title="Chỉnh sửa danh mục"
                                                    >
                                                        Sửa
                                                    </button>
                                                    <button
                                                        className="delete-btn"
                                                        onClick={() => handleDeleteCategory(category.id)}
                                                        title="Xóa danh mục"
                                                    >
                                                        Xóa
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <p className="admin-dashboard-empty">Chưa có danh mục nào.</p>
                        )}
                    </div>
                )}

                {activeTab === 'inventory' && (
                    <div className="admin-dashboard-tab-content">
                        <h3>Quản Lý Tồn Kho</h3>
                        {products.length > 0 ? (
                            <div className="admin-dashboard-table-container">
                                <table className="admin-dashboard-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Tên Sản Phẩm</th>
                                            <th>Số Lượng Tồn Kho</th>
                                            <th>Hành Động</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {products.map(product => (
                                            <tr key={product.id}>
                                                <td>{product.id}</td>
                                                <td>{product.productName}</td>
                                                <td>
                                                    <input
                                                        type="number"
                                                        value={product.quantity}
                                                        onChange={(e) => handleUpdateInventory(product.id, e.target.value)}
                                                        className="admin-dashboard-form-control"
                                                        style={{ width: '100px' }}
                                                        min="0"
                                                    />
                                                </td>
                                                <td className="actions">
                                                    <button
                                                        className="edit-btn"
                                                        onClick={() => handleOpenProductForm(product)}
                                                        title="Chỉnh sửa sản phẩm"
                                                    >
                                                        Sửa
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <p className="admin-dashboard-empty">Chưa có sản phẩm nào.</p>
                        )}
                    </div>
                )}

                {activeTab === 'orders' && (
                    <div className="admin-dashboard-tab-content">
                        <h3>Quản Lý Đơn Hàng</h3>
                        {orders.length > 0 ? (
                            <div className="admin-dashboard-table-container">
                                <table className="admin-dashboard-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Khách Hàng</th>
                                            <th>Tổng Tiền</th>
                                            <th>Ngày Tạo</th>
                                            <th>Trạng Thái</th>
                                            <th>Hành Động</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {orders.map((order) => (
                                            <tr key={order.id}>
                                                <td>{order.id}</td>
                                                <td>{order.customer ? order.customer.firstName + ' ' + order.customer.lastName : 'Không có'}</td>
                                                <td>{order.totalPrice ? order.totalPrice.toLocaleString() : '0'} đ</td>
                                                <td>{parseCreatedAt(order.createdAt)?.toLocaleString() || 'Không có'}</td>
                                                <td>{order.orderStatus ? order.orderStatus.statusName : 'Không có'}</td>
                                                <td className="actions">
                                                    <button
                                                        className="edit-btn"
                                                        onClick={() => navigate(`/admin/order-details/${order.id}`)}
                                                        title="Xem chi tiết đơn hàng"
                                                    >
                                                        Xem
                                                    </button>
                                                    {order.orderStatus?.statusName !== 'Approved' && (
                                                        <button
                                                            className="edit-btn"
                                                            onClick={() => handleApproveOrder(order.id)}
                                                            title="Duyệt đơn hàng"
                                                        >
                                                            Duyệt
                                                        </button>
                                                    )}
                                                    {order.orderStatus?.statusName === 'Approved' && (
                                                        <button
                                                            className="edit-btn"
                                                            onClick={() => handleShipOrder(order.id)}
                                                            title="Đánh dấu giao hàng"
                                                        >
                                                            Giao
                                                        </button>
                                                    )}
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <p className="admin-dashboard-empty">Chưa có đơn hàng nào.</p>
                        )}
                    </div>
                )}

                {activeTab === 'review-posts' && (
                    <div className="admin-dashboard-tab-content">
                        <h3>Quản Lý Bài Viết</h3>
                        <div className="admin-dashboard-actions">
                            <button className="admin-dashboard-add-btn" onClick={() => handleOpenReviewPostForm()}>
                                Thêm Bài Viết
                            </button>
                        </div>
                        {reviewPosts.length > 0 ? (
                            <div className="admin-dashboard-table-container">
                                <table className="admin-dashboard-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Tiêu Đề</th>
                                            <th>Sản Phẩm</th>
                                            <th>Ngày Tạo</th>
                                            <th>Hành Động</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {reviewPosts.map(post => (
                                            <tr key={post.id}>
                                                <td>{post.id}</td>
                                                <td>{post.title}</td>
                                                <td>{post.product ? post.product.productName : 'Không có'}</td>
                                                <td>{parseCreatedAt(post.createdAt)?.toLocaleString() || 'Không có'}</td>
                                                <td className="actions">
                                                    <button
                                                        className="edit-btn"
                                                        onClick={() => handleOpenReviewPostForm(post)}
                                                        title="Chỉnh sửa bài viết"
                                                    >
                                                        Sửa
                                                    </button>
                                                    <button
                                                        className="delete-btn"
                                                        onClick={() => handleDeleteReviewPost(post.id)}
                                                        title="Xóa bài viết"
                                                    >
                                                        Xóa
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <p className="admin-dashboard-empty">Chưa có bài viết nào.</p>
                        )}
                    </div>
                )}

                {activeTab === 'display-areas' && (
                    <div className="admin-dashboard-tab-content">
                        <h3>Quản Lý Khu Vực Hiển Thị</h3>
                        <div className="admin-dashboard-search">
                            <input
                                type="text"
                                placeholder="Tìm kiếm sản phẩm..."
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                className="admin-dashboard-form-control"
                            />
                        </div>
                        {displayAreas.length > 0 ? (
                            displayAreas.map(area => {
                                const totalPages = Math.ceil(filteredProducts.length / productsPerPage);
                                const currentPageForArea = currentPage[area.name] || 1;
                                const startIndex = (currentPageForArea - 1) * productsPerPage;
                                const paginatedProducts = filteredProducts.slice(startIndex, startIndex + productsPerPage);

                                return (
                                    <div key={area.name} className="admin-dashboard-accordion">
                                        <button
                                            className="admin-dashboard-accordion-header"
                                            onClick={() => {
                                                const accordionContent = document.getElementById(`accordion-${area.name}`);
                                                accordionContent.style.display = accordionContent.style.display === 'block' ? 'none' : 'block';
                                            }}
                                        >
                                            {area.label} ({area.products.length} sản phẩm)
                                        </button>
                                        <div id={`accordion-${area.name}`} className="admin-dashboard-accordion-content">
                                            {filteredProducts.length > 0 ? (
                                                <>
                                                    <div className="admin-dashboard-table-container">
                                                        <table className="admin-dashboard-table">
                                                            <thead>
                                                                <tr>
                                                                    <th>ID</th>
                                                                    <th>Tên Sản Phẩm</th>
                                                                    <th>Hiển Thị</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                {paginatedProducts.map(product => (
                                                                    <tr key={product.id}>
                                                                        <td>{product.id}</td>
                                                                        <td>{product.productName}</td>
                                                                        <td>
                                                                            <input
                                                                                type="checkbox"
                                                                                checked={area.products.some(p => p.id === product.id)}
                                                                                onChange={(e) => handleUpdateProductDisplay(product.id, area.name, e.target.checked)}
                                                                            />
                                                                        </td>
                                                                    </tr>
                                                                ))}
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                    <div className="admin-dashboard-pagination">
                                                        {Array.from({ length: totalPages }, (_, index) => (
                                                            <button
                                                                key={index + 1}
                                                                className={`admin-dashboard-page-btn ${currentPageForArea === index + 1 ? 'active' : ''}`}
                                                                onClick={() => handlePageChange(area.name, index + 1)}
                                                            >
                                                                {index + 1}
                                                            </button>
                                                        ))}
                                                    </div>
                                                </>
                                            ) : (
                                                <p className="admin-dashboard-empty">Không tìm thấy sản phẩm nào.</p>
                                            )}
                                        </div>
                                    </div>
                                );
                            })
                        ) : (
                            <p className="admin-dashboard-empty">Chưa có khu vực hiển thị nào.</p>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default AdminDashboard;