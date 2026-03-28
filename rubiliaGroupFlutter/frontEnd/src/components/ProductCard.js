import React from 'react';
import { Link } from 'react-router-dom';
import '../css/ProductCard.css';

const ProductCard = ({ product }) => {
    if (!product) {
        return null;
    }

    // Tính phần trăm giảm giá
    const discountPercentage = product.comparePrice && product.salePrice
        ? Math.round(((product.comparePrice - product.salePrice) / product.comparePrice) * 100)
        : 0;

    // Giới hạn tên sản phẩm (50 ký tự)
    const truncatedName = product.productName && product.productName.length > 50
        ? product.productName.substring(0, 47) + '...'
        : product.productName || 'Không có tên';

    // Lấy URL ảnh từ gallery
    const imageUrl = product.galleries && product.galleries[0]?.image
        ? `http://localhost:8080${product.galleries[0].image}`
        : 'https://via.placeholder.com/150';

    // Lấy số lượng bán ra và số sao từ displayInfos
    const salesCount = product.displayInfos && product.displayInfos.length > 0
        ? product.displayInfos[0].salesCount || 0
        : 0;
    const rating = product.displayInfos && product.displayInfos.length > 0
        ? product.displayInfos[0].rating || 0
        : 0;

    // Tạo ngôi sao đánh giá
    const renderStars = (rating) => {
        const stars = [];
        for (let i = 1; i <= 5; i++) {
            stars.push(
                <span key={i} className={i <= rating ? 'star filled' : 'star'}>★</span>
            );
        }
        return stars;
    };

    return (
        <div className="product-card-wrapper">
            <Link to={`/product/${product.id}`} style={{ textDecoration: 'none' }}>
                <div className="product-card">
                    <div className="product-card-image-wrapper">
                        <img
                            src={imageUrl}
                            className="product-card-image"
                            alt={truncatedName}
                            onError={(e) => {
                                e.target.src = 'https://via.placeholder.com/150?text=Image+Not+Found';
                                e.target.alt = 'Image not found';
                            }}
                        />
                        <div className="product-card-freeship">FREESHIP</div>
                        {discountPercentage > 0 && (
                            <div className="product-card-discount">
                                Giảm {discountPercentage}%
                            </div>
                        )}
                    </div>
                    <div className="product-card-body">
                        <h5 className="product-card-title">{truncatedName}</h5>
                        <div className="product-card-price-section">
                            <p className="product-card-current-price">
                                {product.salePrice ? product.salePrice.toLocaleString() : '0'} 
                            </p>
                            {discountPercentage > 0 && (
                                <p className="product-card-original-price">
                                    {product.comparePrice ? product.comparePrice.toLocaleString() : '0'} 
                                </p>
                            )}
                        </div>
                        <div className="product-card-rating-section">
                            {renderStars(rating)}
                            <span className="product-card-sales-count">
                                Đã bán {salesCount}k
                            </span>
                        </div>
                    </div>
                </div>
            </Link>
        </div>
    );
};

export default ProductCard;