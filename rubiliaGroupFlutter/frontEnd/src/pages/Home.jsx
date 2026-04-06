import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link, useLocation } from 'react-router-dom';
import ProductCard from '../components/ProductCard';
import { handleSendMessage } from './chatbottrain';
import '../css/Home.css';

const Home = () => {
    const [productsByArea, setProductsByArea] = useState({
        flashsale: [],
        trang_diem: [],
        cham_soc_da: [],
        cham_soc_co_the: [],
        phu_kien: [],
        mat_na: [],
        deal_khung_chao_he: [],
        highlighted: [],
    });
    const [latestPosts, setLatestPosts] = useState([]);
    const [isChatOpen, setIsChatOpen] = useState(false);
    const [messages, setMessages] = useState([]);
    const [userInput, setUserInput] = useState('');
    const location = useLocation();

    const banners = [
        { id: 1, image: '/images/banners/main_banner.jpg', alt: 'Main Banner' },
        { id: 2, image: '/images/banners/main_banner2.webp', alt: 'Main Banner 2' },
        { id: 3, image: '/images/banners/main_banner3.webp', alt: 'Main Banner 3' },
        { id: 4, image: '/images/banners/main_banner4.webp', alt: 'Main Banner 4' },
    ];

    const sideBanners = [
        { id: 1, image: '/images/banners/side_banner1.webp', alt: 'Side Banner 1' },
        { id: 2, image: '/images/banners/side_banner2.webp', alt: 'Side Banner 2' },
    ];

    const trendingSearches = [
        { name: 'trang_diem', label: 'Trang Điểm', image: '/images/categories/trang_diem.webp' },
        { name: 'cham_soc_da', label: 'Chăm Sóc Da', image: '/images/categories/cham_soc_da.webp' },
        { name: 'cham_soc_co_the', label: 'Chăm Sóc Cơ Thể', image: '/images/categories/cham_soc_co_the.webp' },
        { name: 'mat_na', label: 'Mặt Nạ', image: '/images/categories/mat_na.webp' },
    ];

    const highlightedBrands = [
        { name: 'Romand', image: '/images/highlight_products/romand.webp', keyword: 'romand' },
        { name: "L'Oréal", image: '/images/highlight_products/loreal.webp', keyword: 'loreal' },
        { name: 'Zeesea', image: '/images/highlight_products/zeesea.webp', keyword: 'Zeesea' },
        { name: 'Cocoon', image: '/images/highlight_products/cocoon.webp', keyword: 'Cocoon' },
        { name: 'HATOMUGI', image: '/images/highlight_products/hatomugi.webp', keyword: 'HATOMUGI' },
        { name: 'NUSE', image: '/images/highlight_products/nuse.webp', keyword: 'NUSE' },
        { name: 'TOOLA', image: '/images/highlight_products/toola.webp', keyword: 'TOOLA' },
    ];

    const highlightedBanner = '/images/banners/highlighted.jpg';

    const faqQuestions = [
        "Flash sale diễn ra khi nào?",
        "Làm sao để mua hàng trên Rubilia.store?",
        "Rubilia.store là trang web của ai?",
        "Liên hệ với Rubilia.store như thế nào?",
        "Cửa hàng Rubilia nằm ở đâu?",
        "Cửa hàng mở cửa lúc mấy giờ?",
        "Chính sách bảo hành của Rubilia thế nào?"
    ];

    const [currentBanner, setCurrentBanner] = useState(0);
    const [lastChange, setLastChange] = useState(Date.now());
    const [timeLeft, setTimeLeft] = useState(12 * 60 * 60);

    useEffect(() => {
        const interval = setInterval(() => {
            const now = Date.now();
            if (now - lastChange >= 7000) {
                setCurrentBanner((prev) => (prev + 1) % banners.length);
                setLastChange(now);
            }
        }, 100);
        return () => clearInterval(interval);
    }, [banners.length, lastChange]);

    useEffect(() => {
        const areas = Object.keys(productsByArea);
        const fetchPromises = areas.map(area =>
            axios.get(`http://rubilia.store/api/products/display-area/${area}`)
                .then(response => {
                    const products = Array.isArray(response.data) ? response.data : [];
                    products.forEach(product => {
                        if (product.displayInfos && product.displayInfos.length > 0) {
                            const displayAreas = product.displayInfos.map(info => info.displayArea);
                            console.log(`Sản phẩm ${product.productName} thuộc các khu vực:`, displayAreas);
                        }
                    });
                    return { area, products };
                })
                .catch(error => {
                    console.error(`Error fetching products for ${area}:`, error);
                    return { area, products: [] };
                })
        );

        Promise.all(fetchPromises).then(results => {
            const updatedProductsByArea = {};
            results.forEach(({ area, products }) => {
                const maxProducts = area === 'highlighted' ? 10 : 12;
                updatedProductsByArea[area] = products.slice(0, maxProducts);
                if (products.length === 0) {
                    console.log(`Không có sản phẩm nào trong khu vực ${area}`);
                }
            });
            setProductsByArea(updatedProductsByArea);
        });
    }, []);

    useEffect(() => {
        const timer = setInterval(() => {
            setTimeLeft(prev => {
                if (prev <= 0) {
                    return 12 * 60 * 60;
                }
                return prev - 1;
            });
        }, 1000);
        return () => clearInterval(timer);
    }, []);

    useEffect(() => {
        const fetchLatestPosts = async () => {
            try {
                const response = await axios.get('http://rubilia.store/api/review-posts');
                const sortedPosts = response.data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                setLatestPosts(sortedPosts.slice(0, 5));
            } catch (err) {
                console.error('Error fetching latest blog posts:', err);
            }
        };
        fetchLatestPosts();
    }, []);

    const formatTime = (seconds) => {
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const secs = seconds % 60;
        return { hours: String(hours).padStart(2, '0'), minutes: String(minutes).padStart(2, '0'), seconds: String(secs).padStart(2, '0') };
    };

    const { hours, minutes, seconds } = formatTime(timeLeft);

    const goToPrevious = () => {
        const now = Date.now();
        setCurrentBanner((prev) => (prev - 1 + banners.length) % banners.length);
        setLastChange(now);
    };

    const goToNext = () => {
        const now = Date.now();
        setCurrentBanner((prev) => (prev + 1) % banners.length);
        setLastChange(now);
    };

    const stripHtml = (html) => {
        const div = document.createElement('div');
        div.innerHTML = html;
        return div.textContent || div.innerText || '';
    };

    const handleFaqClick = (question) => {
        handleSendMessage(question, setMessages, faqQuestions);
    };

    const handleSubmitMessage = (e) => {
        e.preventDefault();
        if (userInput.trim()) {
            handleSendMessage(userInput, setMessages, faqQuestions);
            setUserInput('');
        }
    };

    const areaOrder = [
        'flashsale',
        'cham_soc_da',
        'cham_soc_co_the',
        'trang_diem',
        'phu_kien',
        'mat_na',
        'deal_khung_chao_he',
    ];

    return (
        <div className="home-container">
            {/* Banner Section */}
            <div className="home-banner-section">
                <div className="home-main-banner position-relative">
                    <img
                        src={banners[currentBanner].image}
                        alt={banners[currentBanner].alt}
                        className="home-main-banner-image"
                        onError={(e) => {
                            e.target.src = 'https://via.placeholder.com/800x400?text=Image+Not+Found';
                            e.target.alt = 'Image not found';
                        }}
                    />
                    <button className="home-banner-nav home-prev-btn" onClick={goToPrevious}>
                        <i className="fas fa-chevron-left"></i>
                    </button>
                    <button className="home-banner-nav home-next-btn" onClick={goToNext}>
                        <i className="fas fa-chevron-right"></i>
                    </button>
                </div>
                <div className="home-side-banners">
                    {sideBanners.map(banner => (
                        <img
                            key={banner.id}
                            src={banner.image}
                            alt={banner.alt}
                            className="home-side-banner-image"
                            onError={(e) => {
                                e.target.src = 'https://via.placeholder.com/400x200?text=Image+Not+Found';
                                e.target.alt = 'Image not found';
                            }}
                        />
                    ))}
                </div>
            </div>

            {/* Trending Searches Section */}
            <div className="home-trending-searches-wrapper">
                <div className="home-section-header">
                    <h4 className="home-section-title">Xu Hướng Tìm Kiếm</h4>
                </div>
                <div className="home-trending-searches-section">
                    <div className="home-trending-searches">
                        {trendingSearches.map(search => (
                            <Link key={search.name} to={`/products/${search.name}`} className="home-trending-search-item">
                                <img
                                    src={search.image}
                                    alt={search.label}
                                    className="home-trending-search-image"
                                    onError={(e) => {
                                        e.target.src = 'https://via.placeholder.com/80?text=Image+Not+Found';
                                        e.target.alt = 'Image not found';
                                    }}
                                />
                                <span>{search.label}</span>
                            </Link>
                        ))}
                    </div>
                </div>
            </div>

            {/* Highlighted Products Section */}
            <div className="home-highlighted-section">
                <div className="home-highlighted-left">
                    <Link to="/product/132e217f-3203-483b-8d5e-04e4997a080a">
                        <img
                            src={highlightedBanner}
                            alt="Highlighted Banner"
                            className="home-highlighted-banner"
                            onError={(e) => {
                                e.target.src = '/images/highlight_products/mainpic.png';
                                e.target.alt = 'Image not found';
                            }}
                        />
                    </Link>
                </div>
                <div className="home-highlighted-right">
                    <div className="home-highlighted-content">
                        <div className="home-highlighted-brands">
                            {highlightedBrands.map(brand => (
                                <Link
                                    key={brand.keyword}
                                    to={`/?search=${brand.keyword}`}
                                    className="home-highlighted-brand-item"
                                >
                                    <img
                                        src={brand.image}
                                        alt={brand.name}
                                        className="home-highlighted-brand-image"
                                        onError={(e) => {
                                            e.target.src = 'https://via.placeholder.com/80?text=Brand+Image+Not+Found';
                                            e.target.alt = 'Brand image not found';
                                        }}
                                    />
                                </Link>
                            ))}
                        </div>
                        <div className="home-highlighted-products">
                            <div className="home-section-header">
                                <h4 className="home-section-title">Sản Phẩm Nổi Bật</h4>
                            </div>
                            <div className="row">
                                {productsByArea.highlighted.length > 0 ? (
                                    productsByArea.highlighted.map(product => (
                                        <div key={product.id} className="col-md-2-4">
                                            <ProductCard product={product} />
                                        </div>
                                    ))
                                ) : (
                                    <p>Không có sản phẩm nào trong khu vực này.</p>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Product Sections */}
            {areaOrder
                .filter(area => area !== 'highlighted')
                .map(area => {
                    const products = productsByArea[area] || [];
                    return products.length > 0 && Array.isArray(products) ? (
                        <div key={area} className={`home-product-section ${area === 'flashsale' ? 'flash-sale' : ''} ${area === 'deal_khung_chao_he' ? 'deal_khung_chao_he' : ''}`}>
                            <div className="content-container">
                                <div className="home-section-header">
                                    {area === 'flashsale' ? (
                                        <div className="flashsale-header">
                                            <img
                                                src="/images/flashsale/flashsale.webp"
                                                alt="Flash Sale"
                                                className="flashsale-image"
                                                onError={(e) => {
                                                    e.target.src = 'https://via.placeholder.com/200x50?text=Flash+Sale+Image+Not+Found';
                                                    e.target.alt = 'Flash Sale Image not found';
                                                }}
                                            />
                                            <div className="flashsale-timer">
                                                <span className="timer-box">{hours}</span>
                                                <span className="timer-separator">:</span>
                                                <span className="timer-box">{minutes}</span>
                                                <span className="timer-separator">:</span>
                                                <span className="timer-box">{seconds}</span>
                                            </div>
                                        </div>
                                    ) : (
                                        <h2 className="home-section-title">
                                            {area === 'trang_diem' && 'Trang Điểm'}
                                            {area === 'cham_soc_da' && 'Chăm Sóc Da'}
                                            {area === 'cham_soc_co_the' && 'Chăm Sóc Cơ Thể'}
                                            {area === 'phu_kien' && 'Phụ Kiện'}
                                            {area === 'mat_na' && 'Mặt Nạ'}
                                            {area === 'deal_khung_chao_he' && 'Deal Khủng'}
                                        </h2>
                                    )}
                                    <Link to={`/products/${area}`} className="home-btn-view-all">
                                        {area === 'flashsale' ? 'Xem tất cả các deal' : 'Xem thêm'}
                                    </Link>
                                </div>
                                {(area === 'deal_khung_chao_he' || area === 'cham_soc_da' || area === 'trang_diem' || area === 'cham_soc_co_the') && (
                                    area === 'trang_diem' ? (
                                        <a href="https://www.facebook.com/TNFakeAccount" target="_blank" rel="noopener noreferrer">
                                            <img
                                                src="/images/home/gdragon.png"
                                                alt="Trang Điểm"
                                                className="home-deal-banner"
                                            />
                                        </a>
                                    ) : (
                                        <img
                                            src={
                                                area === 'deal_khung_chao_he' ? "/images/home/deal_khung_chao_he.png" :
                                                area === 'cham_soc_da' ? "/images/home/cham_soc_da_mat.png" :
                                                area === 'cham_soc_co_the' ? "/images/home/cham_soc_co_the.jpg" : ""
                                            }
                                            alt={
                                                area === 'deal_khung_chao_he' ? "Deal Khủng" :
                                                area === 'cham_soc_da' ? "Chăm Sóc Da" :
                                                area === 'cham_soc_co_the' ? "Chăm Sóc Cơ Thể" : ""
                                            }
                                            className="home-deal-banner"
                                            onError={(e) => {
                                                e.target.src = 'https://via.placeholder.com/1600x400?text=Image+Not+Found';
                                                e.target.alt = 'Image not found';
                                            }}
                                        />
                                    )
                                )}
                                <div className="row">
                                    {products.map(product => (
                                        <div key={product.id} className="col-md-2-4 mb-3">
                                            <ProductCard product={product} />
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    ) : (
                        <div key={area} className="home-product-section">
                            <div className="content-container">
                                <div className="home-section-header">
                                    {area === 'flashsale' ? (
                                        <div className="flashsale-header">
                                            <img
                                                src="/images/flashsale/flashsale.webp"
                                                alt="Flash Sale"
                                                className="flashsale-image"
                                                onError={(e) => {
                                                    e.target.src = 'https://via.placeholder.com/200x50?text=Flash+Sale+Image+Not+Found';
                                                    e.target.alt = 'Flash Sale Image not found';
                                                }}
                                            />
                                            <div className="flashsale-timer">
                                                <span className="timer-box">{hours}</span>
                                                <span className="timer-separator">:</span>
                                                <span className="timer-box">{minutes}</span>
                                                <span className="timer-separator">:</span>
                                                <span className="timer-box">{seconds}</span>
                                            </div>
                                        </div>
                                    ) : (
                                        <h2 className="home-section-title">
                                            {area === 'trang_diem' && 'Trang Điểm'}
                                            {area === 'cham_soc_da' && 'Chăm Sóc Da'}
                                            {area === 'cham_soc_co_the' && 'Chăm Sóc Cơ Thể'}
                                            {area === 'phu_kien' && 'Phụ Kiện'}
                                            {area === 'mat_na' && 'Mặt Nạ'}
                                            {area === 'deal_khung_chao_he' && 'Deal Khủng'}
                                        </h2>
                                    )}
                                    <Link to={`/products/${area}`} className="home-btn-view-all">
                                        {area === 'flashsale' ? 'Xem tất cả các deal' : 'Xem thêm'}
                                    </Link>
                                </div>
                                {(area === 'deal_khung_chao_he' || area === 'cham_soc_da' || area === 'trang_diem' || area === 'cham_soc_co_the') && (
                                    area === 'trang_diem' ? (
                                        <a href="https://www.facebook.com/TNFakeAccount" target="_blank" rel="noopener noreferrer">
                                            <img
                                                src="/images/home/gdragon.png"
                                                alt="Trang Điểm"
                                                className="home-deal-banner"
                                            />
                                        </a>
                                    ) : (
                                        <img
                                            src={
                                                area === 'deal_khung_chao_he' ? "/images/home/deal_khung_chao_he.png" :
                                                area === 'cham_soc_da' ? "/images/home/cham_soc_da_mat.png" :
                                                area === 'cham_soc_co_the' ? "/images/home/cham_soc_co_the.jpg" : ""
                                            }
                                            alt={
                                                area === 'deal_khung_chao_he' ? "Deal Khủng" :
                                                area === 'cham_soc_da' ? "Chăm Sóc Da" :
                                                area === 'cham_soc_co_the' ? "Chăm Sóc Cơ Thể" : ""
                                            }
                                            className="home-deal-banner"
                                            onError={(e) => {
                                                e.target.src = 'https://via.placeholder.com/1600x400?text=Image+Not+Found';
                                                e.target.alt = 'Image not found';
                                            }}
                                        />
                                    )
                                )}
                                <p>Không có sản phẩm nào trong khu vực này.</p>
                            </div>
                        </div>
                    );
                })}

            {/* Chatbot Section */}
            <div className="rubilia-chatbot-wrapper">
                <button onClick={() => setIsChatOpen(!isChatOpen)} className="rubilia-chatbot-toggle">
                    <i className="fas fa-comment"></i>
                </button>
                {isChatOpen && (
                    <div className="rubilia-chatbot-container">
                        <div className="rubilia-chatbot-header">
                            <h5>Chatbot Rubilia</h5>
                            <button onClick={() => setIsChatOpen(false)} className="rubilia-chatbot-close">
                                <i className="fas fa-times"></i>
                            </button>
                        </div>
                        <div className="rubilia-chatbot-body">
                            <div className="rubilia-chatbot-faq">
                                <p><strong>Câu hỏi thường gặp:</strong></p>
                                {faqQuestions.map((question, index) => (
                                    <button
                                        key={index}
                                        onClick={() => handleFaqClick(question)}
                                        className="rubilia-chatbot-faq-item"
                                    >
                                        {question}
                                    </button>
                                ))}
                            </div>
                            <div className="rubilia-chatbot-messages">
                                {messages.map((msg, index) => (
                                    <div key={index} className={`rubilia-chatbot-message ${msg.role}`}>
                                        <strong>{msg.role === 'user' ? 'Bạn: ' : 'Bot: '}</strong>
                                        {msg.content}
                                    </div>
                                ))}
                            </div>
                        </div>
                        <form onSubmit={handleSubmitMessage} className="rubilia-chatbot-input-form">
                            <input
                                type="text"
                                value={userInput}
                                onChange={(e) => setUserInput(e.target.value)}
                                placeholder="Nhập tin nhắn..."
                                className="rubilia-chatbot-input"
                            />
                            <button type="submit" className="rubilia-chatbot-send">
                                <i className="fas fa-paper-plane"></i>
                            </button>
                        </form>
                    </div>
                )}
            </div>

            {/* Footer Section */}
            <footer className="home-footer">
                <div className="home-footer-content">
                    <div className="home-blog-posts">
                        <div className="home-blog-posts-header">
                            <h2 className="home-blog-posts-title">TIN TỨC & CẨM NANG LÀM ĐẸP</h2>
                            <Link to="/blog_lam_dep" className="home-btn-view-all">
                                Xem thêm
                            </Link>
                        </div>
                        <div className="row">
                            {latestPosts.length > 0 ? (
                                latestPosts.map(post => (
                                    <div key={post.id} className="col-md-3 mb-4">
                                        <Link to={`/blog/${post.id}`}>
                                            <div className="blog-card">
                                                <div className="blog-card-image-wrapper">
                                                    <img
                                                        src={post.imageUrl || 'https://via.placeholder.com/300?text=Blog+Image'}
                                                        alt={stripHtml(post.title)}
                                                        className="blog-card-image"
                                                        onError={(e) => {
                                                            e.target.src = 'https://via.placeholder.com/300?text=Image+Not+Found';
                                                            e.target.alt = 'Image not found';
                                                        }}
                                                    />
                                                </div>
                                                <div className="blog-card-body">
                                                    <h5 className="blog-card-title">{stripHtml(post.title) || 'Không có tiêu đề'}</h5>
                                                    <p className="blog-card-date">{new Date(post.createdAt).toLocaleDateString('vi-VN') || 'Không có ngày đăng'}</p>
                                                    <p className="blog-card-excerpt">{post.content?.substring(0, 100) + '...' || 'Không có tóm tắt'}</p>
                                                </div>
                                            </div>
                                        </Link>
                                    </div>
                                ))
                            ) : (
                                <p className="blog-list-empty">Chưa có bài viết nào.</p>
                            )}
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default Home;