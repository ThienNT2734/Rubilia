import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { getCartItemCount } from '../utils/cartUtils';
import { switchUser, logout, loginGoogle, registerOAuth2 } from '../utils/auth';

const Navbar = () => {
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [showSearchResults, setShowSearchResults] = useState(false);
    const [cartCount, setCartCount] = useState(0);
    const [showCategoryPopup, setShowCategoryPopup] = useState(false);
    const [showAuthPopup, setShowAuthPopup] = useState(false);
    const [authMode, setAuthMode] = useState('login');
    const [showPassword, setShowPassword] = useState(false);
    const [formData, setFormData] = useState({ email: '', first_name: '', last_name: '', password: '', confirmPassword: '' });
    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('customer'));
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [oauth2Data, setOauth2Data] = useState(null);
    const navigate = useNavigate();
    const popupRef = useRef(null);
    const authPopupRef = useRef(null);
    const searchResultsRef = useRef(null);

    const updateCartCount = () => {
        const count = getCartItemCount();
        setCartCount(count);
    };

    useEffect(() => {
        updateCartCount();
        const interval = setInterval(updateCartCount, 1000);
        const handleCartUpdate = () => {
            updateCartCount();
        };
        window.addEventListener('cartUpdated', handleCartUpdate);
        return () => {
            clearInterval(interval);
            window.removeEventListener('cartUpdated', handleCartUpdate);
        };
    }, []);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (popupRef.current && !popupRef.current.contains(event.target)) {
                setShowCategoryPopup(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    useEffect(() => {
        const handleClickOutsideAuth = (event) => {
            if (authPopupRef.current && !authPopupRef.current.contains(event.target)) {
                setShowAuthPopup(false);
                setAuthMode('login');
                setFormData({ email: '', first_name: '', last_name: '', password: '', confirmPassword: '' });
                setShowPassword(false);
                setError('');
                setSuccess('');
                setOauth2Data(null);
            }
        };
        document.addEventListener('mousedown', handleClickOutsideAuth);
        return () => {
            document.removeEventListener('mousedown', handleClickOutsideAuth);
        };
    }, []);

    useEffect(() => {
        const handleClickOutsideSearch = (event) => {
            if (searchResultsRef.current && !searchResultsRef.current.contains(event.target)) {
                setShowSearchResults(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutsideSearch);
        return () => {
            document.removeEventListener('mousedown', handleClickOutsideSearch);
        };
    }, []);

    useEffect(() => {
        if (searchQuery.trim() === '') {
            setSearchResults([]);
            setShowSearchResults(false);
            return;
        }

        const fetchSearchResults = async () => {
            try {
                const response = await axios.get(`https://rubilia.store/api/products/search?keyword=${encodeURIComponent(searchQuery)}`);
                const results = response.data.slice(0, 5);
                setSearchResults(results);
                setShowSearchResults(true);
            } catch (error) {
                console.error('Lỗi tìm kiếm sản phẩm:', error);
                setSearchResults([]);
                setShowSearchResults(false);
            }
        };

        const debounce = setTimeout(() => {
            fetchSearchResults();
        }, 300);

        return () => clearTimeout(debounce);
    }, [searchQuery]);

    const handleSearch = (e) => {
        e.preventDefault();
        setShowSearchResults(false);
        if (searchQuery.trim()) {
            navigate(`/?search=${encodeURIComponent(searchQuery)}`);
        }
    };

    const handleSwitchUser = () => {
        switchUser();
        updateCartCount();
        alert('Đã chuyển sang tài khoản mới!');
    };

    const handleLogout = () => {
        logout();
        setIsLoggedIn(false);
        alert('Đã đăng xuất!');
        navigate('/');
    };

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        try {
            if (authMode === 'login') {
                const response = await axios.post('https://rubilia.store/api/customers/login', {
                    user_name: formData.email,
                    password_hash: formData.password
                });
                console.log('Đăng nhập thành công:', response.data);
                alert('Đăng nhập thành công!');
                setIsLoggedIn(true);
                localStorage.setItem('customer', JSON.stringify(response.data));
            } else if (authMode === 'register') {
                if (formData.password !== formData.confirmPassword) {
                    setError('Mật khẩu xác nhận không khớp!');
                    return;
                }
                if (!formData.first_name.trim()) {
                    setError('Vui lòng nhập tên!');
                    return;
                }
                if (!formData.last_name.trim()) {
                    setError('Vui lòng nhập họ!');
                    return;
                }
                const response = await axios.post('https://rubilia.store/api/customers/register', {
                    user_name: formData.email,
                    password_hash: formData.password,
                    first_name: formData.first_name,
                    last_name: formData.last_name
                });
                console.log('Đăng ký thành công:', response.data);
                alert('Đăng ký thành công! Vui lòng đăng nhập.');
                setAuthMode('login');
            } else if (authMode === 'oauth2_register') {
                const response = await registerOAuth2(formData.email, formData.first_name, formData.last_name);
                console.log('Đăng ký OAuth2 thành công:', response);
                alert('Đăng ký OAuth2 thành công!');
                setIsLoggedIn(true);
            } else if (authMode === 'forgot') {
                const response = await axios.post('https://rubilia.store/api/customers/forgot-password', {
                    email: formData.email
                });
                setSuccess('Email đặt lại mật khẩu đã được gửi! Vui lòng kiểm tra hộp thư của bạn.');
                setTimeout(() => {
                    setShowAuthPopup(false);
                    setAuthMode('login');
                }, 3000);
            }
            if (authMode !== 'forgot') {
                setShowAuthPopup(false);
            }
            setFormData({ email: '', first_name: '', last_name: '', password: '', confirmPassword: '' });
            setShowPassword(false);
            setOauth2Data(null);
        } catch (error) {
            console.error('Lỗi:', error.response?.data || error.message);
            if (error.response?.status === 405) {
                setError('API không hỗ trợ. Vui lòng kiểm tra backend.');
            } else if (error.response?.status === 400) {
                setError(error.response.data || 'Thông tin không hợp lệ.');
            } else if (error.code === 'ERR_NETWORK') {
                setError('Không kết nối được với server. Vui lòng kiểm tra backend.');
            } else {
                setError('Đã có lỗi xảy ra. Vui lòng thử lại.');
            }
        }
    };

    const handleGoogleLogin = async () => {
        try {
            const result = await loginGoogle();
            if (result.status === 'info_required') {
                setOauth2Data(result.data);
                setFormData({
                    email: result.data.email || '',
                    first_name: result.data.firstName || '',
                    last_name: result.data.lastName || '',
                    password: '',
                    confirmPassword: ''
                });
                setAuthMode('oauth2_register');
            } else {
                setIsLoggedIn(true);
                setShowAuthPopup(false);
            }
        } catch (error) {
            setError(error.message || 'Đăng nhập bằng Google thất bại.');
        }
    };

    const categoryLinks = [
        { name: "Chăm sóc da mặt", path: "/products/cham_soc_da" },
        { name: "Trang điểm", path: "/products/trang_diem" },
        { name: "Chăm sóc cơ thể", path: "/products/cham_soc_co_the" },
        { name: "Chăm sóc sức khỏe", path: "/products/cham_soc_suc_khoe" },
        { name: "Chăm sóc tóc", path: "/products/cham_soc_toc" },
        { name: "Nước hoa", path: "/products/nuoc_hoa" },
        { name: "Dụng cụ làm đẹp", path: "/products/dung_cu_lam_dep" },
        { name: "Sản phẩm cho nam", path: "/products/san_pham_cho_nam" },
        { name: "Sản phẩm cho bé", path: "/products/san_pham_cho_be" },
        { name: "Thực phẩm chức năng", path: "/products/thuc_pham_chuc_nang" },
    ];

    const defaultCategories = [
        { name: "Chăm sóc da mặt", path: "/products/cham_soc_da" },
        { name: "Trang điểm", path: "/products/trang_diem" },
        { name: "Chăm sóc cơ thể", path: "/products/cham_soc_co_the" },
        { name: "Deal khủng chào hè", path: "/products/deal_khung_chao_he" },
        { name: "Blog làm đẹp", path: "/blog_lam_dep" },
    ];

    return (
        <div className="navbar-wrapper">
            <div className="header-gif">
                <img src={require('./images/_TopHeader_1_.gif')} 
                alt="Header GIF" 
                className="header-gif-image" />
                
            </div>

            <nav className="navbar navbar-expand-lg navbar-light bg-warning py-3">
                <div className="container">
                    <Link className="navbar-brand" to="/">
                        <img src={require('./images/logo.png')} alt="Rubilia Logo" className="navbar-logo" />
                    </Link>
                    <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                        <span className="navbar-toggler-icon"></span>
                    </button>
                    <div className="collapse navbar-collapse" id="navbarNav">
                        <form className="d-flex mx-auto search-form position-relative" onSubmit={handleSearch}>
                            <input
                                className="form-control search-input"
                                type="search"
                                placeholder="Voucher Ngập Tràn & FREESHIP Toàn Quốc"
                                aria-label="Search"
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                            />
                            <button className="btn btn-search" type="submit">
                                <i className="fas fa-search"></i>
                            </button>
                            {showSearchResults && searchResults.length > 0 && (
                                <div className="search-results-dropdown" ref={searchResultsRef}>
                                    {searchResults.map(product => (
                                        <Link
                                            key={product.id}
                                            to={`/product/${product.id}`}
                                            className="search-result-item"
                                            onClick={() => setShowSearchResults(false)}
                                        >
                                            <img
                                                src={product.galleries?.[0]?.image ? `https://rubilia.store${product.galleries[0].image}` : 'https://via.placeholder.com/50'}
                                                alt={product.productName}
                                                className="search-result-img"
                                                onError={(e) => {
                                                    e.target.src = 'https://via.placeholder.com/50?text=Image+Not+Found';
                                                    e.target.alt = 'Image not found';
                                                }}
                                            />
                                            <div className="search-result-info">
                                                <span className="search-result-name">{product.productName}</span>
                                                <span className="search-result-price">
                                                    ₫{(product.price || product.salePrice) ? (product.price || product.salePrice).toLocaleString() : '0'}
                                                </span>
                                            </div>
                                        </Link>
                                    ))}
                                </div>
                            )}
                        </form>

                        <ul className="navbar-nav mx-auto">
                            <li className="nav-item">
                                <Link className="nav-link" to="/">Khuyến mãi</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link" to="/">Gợi ý</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link" to="/">Thương hiệu</Link>
                            </li>
                        </ul>

                        <div className="navbar-actions d-flex align-items-center gap-2">
                            {!isLoggedIn && (
                                <button
                                    className="d-flex align-items-center px-3 py-2 text-white rounded-pill text-decoration-none"
                                    style={{ backgroundColor: '#f78b1f', height: '48px', border: 'none' }}
                                    onClick={() => setShowAuthPopup(true)}
                                >
                                    <i className="fas fa-user me-2"></i>
                                    Đăng nhập / Đăng ký
                                </button>
                            )}
                            {isLoggedIn && (
                                <>
                                    <Link
                                        to="/user/profile"
                                        className="d-flex align-items-center px-3 py-2 text-white rounded-pill text-decoration-none"
                                        style={{ backgroundColor: '#f78b1f', height: '48px' }}
                                    >
                                        <i className="fas fa-user me-2"></i>
                                        Tài khoản
                                    </Link>
                                    <button
                                        className="d-flex align-items-center px-3 py-2 text-white rounded-pill text-decoration-none"
                                        style={{ backgroundColor: '#f78b1f', height: '48px', border: 'none' }}
                                        onClick={handleLogout}
                                    >
                                        <i className="fas fa-sign-out-alt me-2"></i>
                                        Đăng xuất
                                    </button>
                                </>
                            )}
                            <Link
                                to="/cart"
                                className="d-flex align-items-center px-3 py-2 text-white rounded-pill text-decoration-none position-relative"
                                style={{ backgroundColor: '#f78b1f', height: '48px' }}
                            >
                                <i className="fas fa-shopping-bag me-2"></i>
                                <div className="text-start">
                                    <div style={{ fontSize: '0.7rem', lineHeight: '1rem' }}>GIỎ HÀNG</div>
                                </div>
                                {cartCount >= 1 && (
                                    <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger cart-badge">
                                        {cartCount}
                                    </span>
                                )}
                            </Link>
                        </div>
                    </div>
                </div>
            </nav>

            <div className="category-menu">
                <div className="container">
                    <ul className="category-list d-flex align-items-center gap-4">
                        <li
                            className="category-item position-relative"
                            onMouseEnter={() => setShowCategoryPopup(true)}
                            onMouseLeave={() => setShowCategoryPopup(false)}
                        >
                            <button className="category-btn">
                                <i className="fas fa-bars me-2"></i> Tất cả danh mục
                            </button>
                            {showCategoryPopup && (
                                <div className="category-popup" ref={popupRef}>
                                    <ul className="category-popup-list">
                                        {categoryLinks.map((category, index) => (
                                            <li key={index}>
                                                <Link
                                                    to={category.path}
                                                    className="category-popup-item"
                                                    onClick={() => setShowCategoryPopup(false)}
                                                >
                                                    {category.name}
                                                </Link>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            )}
                        </li>
                        {!showCategoryPopup && defaultCategories.map((category, index) => (
                            <li key={index} className="category-item">
                                <Link to={category.path} className="category-link">
                                    {category.name}
                                </Link>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>

            {showAuthPopup && (
                <>
                    <div className="auth-overlay"></div>
                    <div className="auth-popup" ref={authPopupRef}>
                        <div className="auth-form">
                            <h3 className="auth-title">
                                {authMode === 'login' ? 'Đăng nhập' : authMode === 'register' ? 'Đăng ký' : authMode === 'oauth2_register' ? 'Hoàn tất đăng ký' : 'Quên mật khẩu'}
                            </h3>
                            {error && <p className="text-danger">{error}</p>}
                            {success && <p className="text-success">{success}</p>}
                            <form onSubmit={handleSubmit}>
                                {authMode === 'oauth2_register' && (
                                    <>
                                        <div className="form-group">
                                            <label htmlFor="email">Email</label>
                                            <input
                                                type="email"
                                                id="email"
                                                name="email"
                                                value={formData.email}
                                                onChange={handleInputChange}
                                                className="form-control"
                                                required
                                                disabled={!!oauth2Data?.email}
                                            />
                                        </div>
                                    </>
                                )}
                                {(authMode === 'login' || authMode === 'register' || authMode === 'forgot') && (
                                    <div className="form-group">
                                        <label htmlFor="email">Email</label>
                                        <input
                                            type="email"
                                            id="email"
                                            name="email"
                                            value={formData.email}
                                            onChange={handleInputChange}
                                            className="form-control"
                                            required
                                        />
                                    </div>
                                )}
                                {(authMode === 'register' || authMode === 'oauth2_register') && (
                                    <>
                                        <div className="form-group">
                                            <label htmlFor="first_name">Tên</label>
                                            <input
                                                type="text"
                                                id="first_name"
                                                name="first_name"
                                                value={formData.first_name}
                                                onChange={handleInputChange}
                                                className="form-control"
                                                required
                                                disabled={!!oauth2Data?.firstName}
                                            />
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="last_name">Họ</label>
                                            <input
                                                type="text"
                                                id="last_name"
                                                name="last_name"
                                                value={formData.last_name}
                                                onChange={handleInputChange}
                                                className="form-control"
                                                required
                                                disabled={!!oauth2Data?.lastName}
                                            />
                                        </div>
                                    </>
                                )}
                                {authMode !== 'forgot' && authMode !== 'oauth2_register' && (
                                    <div className="form-group position-relative">
                                        <label htmlFor="password">Mật khẩu</label>
                                        <input
                                            type={showPassword ? 'text' : 'password'}
                                            id="password"
                                            name="password"
                                            value={formData.password}
                                            onChange={handleInputChange}
                                            className="form-control"
                                            required
                                        />
                                        <button
                                            type="button"
                                            className="password-toggle"
                                            onClick={() => setShowPassword(!showPassword)}
                                        >
                                            <i className={showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'}></i>
                                        </button>
                                    </div>
                                )}
                                {authMode === 'register' && (
                                    <div className="form-group position-relative">
                                        <label htmlFor="confirmPassword">Xác nhận mật khẩu</label>
                                        <input
                                            type={showPassword ? 'text' : 'password'}
                                            id="confirmPassword"
                                            name="confirmPassword"
                                            value={formData.confirmPassword}
                                            onChange={handleInputChange}
                                            className="form-control"
                                            required
                                        />
                                        <button
                                            type="button"
                                            className="password-toggle"
                                            onClick={() => setShowPassword(!showPassword)}
                                        >
                                            <i className={showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'}></i>
                                        </button>
                                    </div>
                                )}
                                {authMode !== 'oauth2_register' && (
                                    <>
                                        <button type="submit" className="btn btn-primary w-100 mt-3">
                                            {authMode === 'login' ? 'Đăng nhập' : authMode === 'register' ? 'Đăng ký' : 'Gửi email đặt lại'}
                                        </button>
                                        <div className="auth-social-buttons mt-3">
                                            <button
                                                type="button"
                                                className="btn btn-danger w-100 mb-2"
                                                onClick={handleGoogleLogin}
                                            >
                                                <i className="fab fa-google me-2"></i> Đăng nhập bằng Google
                                            </button>
                                        </div>
                                    </>
                                )}
                                {authMode === 'oauth2_register' && (
                                    <button type="submit" className="btn btn-primary w-100 mt-3">
                                        Hoàn tất đăng ký
                                    </button>
                                )}
                            </form>

                            <div className="auth-links mt-3 text-center">
                                {authMode !== 'login' && authMode !== 'oauth2_register' && (
                                    <button
                                        className="auth-link"
                                        onClick={() => {
                                            setAuthMode('login');
                                            setOauth2Data(null);
                                        }}
                                    >
                                        Đăng nhập
                                    </button>
                                )}
                                {authMode !== 'register' && authMode !== 'oauth2_register' && (
                                    <button
                                        className="auth-link"
                                        onClick={() => {
                                            setAuthMode('register');
                                            setOauth2Data(null);
                                        }}
                                    >
                                        Đăng ký
                                    </button>
                                )}
                                {authMode !== 'forgot' && authMode !== 'oauth2_register' && (
                                    <button
                                        className="auth-link"
                                        onClick={() => {
                                            setAuthMode('forgot');
                                            setOauth2Data(null);
                                        }}
                                    >
                                        Quên mật khẩu?
                                    </button>
                                )}
                            </div>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
};

export default Navbar;