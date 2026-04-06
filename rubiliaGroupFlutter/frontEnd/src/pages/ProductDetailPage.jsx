import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { addToCart } from '../utils/cartUtils';
import { isAuthenticated, getCurrentUserEmail } from '../utils/auth';
import ProductCard from '../components/ProductCard'; // Thêm import ProductCard
import '../css/ProductDetailPage.css';

const ProductDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [suggestedProducts, setSuggestedProducts] = useState([]);
    const [comments, setComments] = useState([]);
    const [ratings, setRatings] = useState([]);
    const [error, setError] = useState(null);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [isExpanded, setIsExpanded] = useState(false);
    const [needsToggle, setNeedsToggle] = useState(false);
    const [carouselPosition, setCarouselPosition] = useState(0);
    const [notification, setNotification] = useState(null);
    const descriptionRef = useRef(null);

    const [commentContent, setCommentContent] = useState('');
    const [ratingValue, setRatingValue] = useState(0);

    useEffect(() => {
        axios.get(`http://localhost:8080/api/products/${id}`)
            .then(response => {
                setProduct(response.data);

                axios.get('http://localhost:8080/api/products')
                    .then(res => {
                        const allProducts = res.data;
                        const keyword = response.data.productName.toLowerCase().split(' ')[0];
                        const filteredProducts = allProducts
                            .filter(p => 
                                p.id !== response.data.id &&
                                p.productName.toLowerCase().includes(keyword)
                            )
                            .slice(0, 10);
                        setSuggestedProducts(filteredProducts);
                    });

                axios.get(`http://localhost:8080/api/comments/product/${id}`)
                    .then(res => setComments(res.data));

                axios.get(`http://localhost:8080/api/ratings/product/${id}`)
                    .then(res => setRatings(res.data));
            })
            .catch(error => {
                setError('Không tìm thấy sản phẩm.');
            });
    }, [id]);

    useEffect(() => {
        if (descriptionRef.current) {
            const lineHeight = parseFloat(getComputedStyle(descriptionRef.current).lineHeight);
            const maxLines = 8;
            const maxHeight = lineHeight * maxLines;
            setNeedsToggle(descriptionRef.current.scrollHeight > maxHeight);
        }
    }, [product]);

    useEffect(() => {
        if (notification) {
            const timer = setTimeout(() => setNotification(null), 3500);
            return () => clearTimeout(timer);
        }
    }, [notification]);

    const showNotification = (message) => {
        setNotification(message);
    };

    const handleAddToCart = () => {
        if (product) {
            addToCart({
                id: product.id,
                productName: product.productName,
                salePrice: product.salePrice,
                image: product.galleries?.[0]?.image
            });
            showNotification('Đã thêm vào giỏ hàng!');
        }
    };

    const handlePrevImage = () => {
        setCurrentImageIndex(prev => 
            prev === 0 ? (product.galleries.length - 1) : prev - 1
        );
    };

    const handleNextImage = () => {
        setCurrentImageIndex(prev => 
            prev === (product.galleries.length - 1) ? 0 : prev + 1
        );
    };

    const handleThumbnailClick = (index) => {
        setCurrentImageIndex(index);
    };

    const toggleDescription = () => {
        setIsExpanded(!isExpanded);
    };

    const handleCarouselPrev = () => {
        setCarouselPosition(prev => Math.max(prev - 1, 0));
    };

    const handleCarouselNext = () => {
        setCarouselPosition(prev => Math.min(prev + 1, Math.ceil(suggestedProducts.length / 4) - 1));
    };

    const handleCommentSubmit = async (e) => {
        e.preventDefault();
        if (!isAuthenticated()) {
            showNotification('Vui lòng đăng nhập để gửi bình luận!');
            navigate('/login');
            return;
        }
        if (!commentContent.trim()) {
            showNotification('Vui lòng nhập nội dung bình luận.');
            return;
        }
        try {
            const email = getCurrentUserEmail();
            const commentData = { content: commentContent, productId: id };
            await axios.post('http://localhost:8080/api/comments', commentData, {
                params: { email }
            });
            showNotification('Bình luận đã được gửi và đang chờ duyệt.');
            setCommentContent('');
            const res = await axios.get(`http://localhost:8080/api/comments/product/${id}`);
            setComments(res.data);
        } catch (err) {
            showNotification('Lỗi khi gửi bình luận.');
        }
    };

    const handleRatingSubmit = async (rating) => {
        if (!isAuthenticated()) {
            showNotification('Vui lòng đăng nhập để gửi đánh giá!');
            navigate('/login');
            return;
        }
        try {
            const email = getCurrentUserEmail();
            const ratingData = { rating: parseFloat(rating), productId: id };
            await axios.post('http://localhost:8080/api/ratings', ratingData, {
                params: { email }
            });
            showNotification('Đánh giá đã được gửi và đang chờ duyệt.');
            setRatingValue(0);
            const res = await axios.get(`http://localhost:8080/api/ratings/product/${id}`);
            setRatings(res.data);
        } catch (err) {
            showNotification('Lỗi khi gửi đánh giá.');
        }
    };

    const getEmailPrefix = (email) => {
        return email ? email.split('@')[0] : 'Người dùng ẩn danh';
    };

    if (error) return (
        <div className="container my-5">
            <p className="error-text">{error}</p>
        </div>
    );

    if (!product) return (
        <div className="container my-5">
            <div className="row">
                <div className="col-md-6">
                    <div className="skeleton skeleton-img"></div>
                </div>
                <div className="col-md-6">
                    <div className="skeleton skeleton-text"></div>
                    <div className="skeleton skeleton-text"></div>
                    <div className="skeleton skeleton-text"></div>
                </div>
            </div>
        </div>
    );

    return (
        <div className="container my-5">
            {notification && (
                <div className="notification-popup">
                    {notification}
                </div>
            )}
            <div className="row">
                <div className="col-md-6">
                    <div className="image-gallery">
                        {product.galleries && product.galleries.length > 0 ? (
                            <img
                                src={`http://localhost:8080${product.galleries[currentImageIndex].image}`}
                                alt={product.productName || 'Sản phẩm'}
                                className="product-detail-img"
                                loading="lazy"
                                onError={(e) => {
                                    e.target.src = 'https://placehold.co/600x600?text=Image+Not+Found';
                                    e.target.alt = 'Image not found';
                                }}
                            />
                        ) : (
                            <img
                                src="https://placehold.co/600x600?text=Image+Not+Found"
                                alt="No Image"
                                className="product-detail-img"
                                loading="lazy"
                            />
                        )}
                        {product.galleries && product.galleries.length > 1 && (
                            <>
                                <button
                                    className="gallery-nav prev"
                                    onClick={handlePrevImage}
                                >
                                    <i className="fas fa-chevron-left"></i>
                                </button>
                                <button
                                    className="gallery-nav next"
                                    onClick={handleNextImage}
                                >
                                    <i className="fas fa-chevron-right"></i>
                                </button>
                            </>
                        )}
                    </div>
                    {product.galleries && product.galleries.length > 0 && (
                        <div className="thumbnail-container">
                            {product.galleries.map((img, index) => (
                                <img
                                    key={index}
                                    src={`http://localhost:8080${img.image}`}
                                    alt={`Thumbnail ${index + 1}`}
                                    className={`thumbnail ${index === currentImageIndex ? 'active' : ''}`}
                                    onClick={() => handleThumbnailClick(index)}
                                    loading="lazy"
                                    onError={(e) => {
                                        e.target.src = 'https://placehold.co/100x100?text=Image+Not+Found';
                                        e.target.alt = 'Image not found';
                                    }}
                                />
                            ))}
                        </div>
                    )}
                </div>
                <div className="col-md-6">
                    <h1 className="product-title">{product.productName || 'Không có tên'}</h1>
                    <p className="product-short-desc">
                        {product.shortDescription || 'Không có mô tả ngắn.'}
                    </p>
                    <div className="price-section">
                        {product.comparePrice > 0 && (
                            <p className="compare-price">
                                Giá gốc: ₫{product.comparePrice.toLocaleString()}
                            </p>
                        )}
                        <p className="sale-price">
                            Giá hiện tại: {product.salePrice ? product.salePrice.toLocaleString() : '0'}
                        </p>
                        {product.comparePrice > 0 && (
                            <p className="discount">
                                Giảm giá: {Math.round(((product.comparePrice - product.salePrice) / product.comparePrice) * 100)}%
                            </p>
                        )}
                    </div>
                    <div className="extra-info">
                        <div className="info-item">
                            <i className="fas fa-warehouse"></i>
                            <span>Còn hàng</span>
                        </div>
                        <div className="info-item">
                            <i className="fas fa-shipping-fast"></i>
                            <span>Giao hàng miễn phí</span>
                        </div>
                        <div className="info-item">
                            <i className="fas fa-shield-alt"></i>
                            <span>Bảo hành 30 ngày</span>
                        </div>
                    </div>
                    <button className="add-to-cart-btn" onClick={handleAddToCart}>
                        <i className="fas fa-cart-plus"></i> Thêm vào giỏ hàng
                    </button>
                </div>
            </div>

            <div className="section description-section">
                <h2>Mô tả sản phẩm</h2>
                <div className="description-container">
                    <div
                        ref={descriptionRef}
                        className={`product-description ${!isExpanded ? 'collapsed' : ''}`}
                        dangerouslySetInnerHTML={{ __html: product.productDescription || 'Không có mô tả' }}
                    />
                    {needsToggle && !isExpanded && (
                        <div className="toggle-description-wrapper">
                            <span
                                className="toggle-description"
                                onClick={toggleDescription}
                            >
                                Xem thêm
                            </span>
                        </div>
                    )}
                </div>
            </div>

            <div className="section comment-rating-section">
                <h2>Đánh giá & Bình luận</h2>
                <div className="comment-rating-form">
                    <div className="rating-group">
                        <label>Đánh giá của bạn:</label>
                        <div className="star-rating-input">
                            {[1, 2, 3, 4, 5].map(star => (
                                <i
                                    key={star}
                                    className={`fas fa-star ${star <= ratingValue ? 'selected' : ''}`}
                                    onClick={() => {
                                        setRatingValue(star);
                                        handleRatingSubmit(star);
                                    }}
                                />
                            ))}
                        </div>
                    </div>
                    <div className="comment-group">
                        <textarea
                            value={commentContent}
                            onChange={(e) => setCommentContent(e.target.value)}
                            placeholder="Viết bình luận của bạn..."
                        />
                        <button className="submit-comment-btn" onClick={handleCommentSubmit}>
                            Gửi bình luận
                        </button>
                    </div>
                </div>
            </div>

            <div className="section reviews-section">
                <h2>Đánh giá khách hàng</h2>
                {ratings.length > 0 ? (
                    ratings.map(rating => (
                        <div key={rating.id} className="review-item">
                            <div className="review-header">
                                <span className="reviewer-name">
                                    {getEmailPrefix(rating.email)}
                                </span>
                                <div className="star-rating">
                                    {[...Array(5)].map((_, i) => (
                                        <i
                                            key={i}
                                            className={`fas fa-star ${i < Math.round(rating.rating) ? 'selected' : 'far'}`}
                                        />
                                    ))}
                                </div>
                                <span className="review-date">
                                    {new Date(rating.createdAt).toLocaleDateString('vi-VN')}
                                </span>
                            </div>
                        </div>
                    ))
                ) : (
                    <p>Chưa có đánh giá nào.</p>
                )}
            </div>

            <div className="section comments-section">
                <h2>Bình luận khách hàng</h2>
                {comments.length > 0 ? (
                    comments.map(comment => (
                        <div key={comment.id} className="comment-item">
                            <div className="comment-header">
                                <span className="commenter-name">
                                    {getEmailPrefix(comment.email)}
                                </span>
                                <span className="comment-date">
                                    {new Date(comment.createdAt).toLocaleDateString('vi-VN')}
                                </span>
                            </div>
                            <p>{comment.content}</p>
                        </div>
                    ))
                ) : (
                    <p>Chưa có bình luận nào.</p>
                )}
            </div>

            {suggestedProducts.length > 0 && (
                <div className="section suggested-products-section">
                    <h2>Sản phẩm gợi ý</h2>
                    <div className="suggested-products-container">
                        <button
                            className="carousel-btn prev-btn"
                            onClick={handleCarouselPrev}
                            disabled={carouselPosition === 0}
                        >
                            <i className="fas fa-chevron-left"></i>
                        </button>
                        <div className="suggested-products-carousel">
                            <div
                                className="suggested-products-list"
                                style={{ transform: `translateX(-${carouselPosition * 100}%)` }}
                            >
                                {suggestedProducts.map(suggested => (
                                    <div key={suggested.id} className="suggested-product-item">
                                        <ProductCard product={suggested} />
                                    </div>
                                ))}
                            </div>
                        </div>
                        <button
                            className="carousel-btn next-btn"
                            onClick={handleCarouselNext}
                            disabled={carouselPosition >= Math.ceil(suggestedProducts.length / 4) - 1}
                        >
                            <i className="fas fa-chevron-right"></i>
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ProductDetailPage;