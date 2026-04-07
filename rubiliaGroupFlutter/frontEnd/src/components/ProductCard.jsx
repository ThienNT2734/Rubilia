import React from 'react';
import { Link } from 'react-router-dom';
import '../css/ProductCard.css';

const ProductCard = ({ product }) => {
    if (!product) return null;

    // 1. ÉP KIỂU SỐ (Số thực): CHỈ lấy 2 trường: comparePrice và discountPercentage
    const originalPrice = parseFloat(product.comparePrice || 0);
    const discountPercent = product.discountPercentage ? parseFloat(product.discountPercentage) : 0;

    // XÓA HOÀN TOÀN SALE PRICE - KHÔNG DÙNG product.salePrice hay product.price NỮA
    // 2. TÍNH GIÁ SAU GIẢM: DUY NHẤT theo công thức: comparePrice * (100 - discount_percentage) / 100
    const finalPrice = discountPercent > 0
        ? Math.round(originalPrice * (1 - discountPercent / 100))
        : originalPrice;

    // 3. ĐIỀU KIỆN HIỆN SALE: CHỈ khi discountPercent lớn hơn 0
    const hasPromotion = discountPercent > 0;
    
    // FORCE DEBUG
    console.log('FINAL CHECK:', {
        name: product.productName,
        discountPercent,
        originalPrice,
        finalPrice,
        hasPromotion
    });

    // --- Debug giúp Yến nhìn lỗi ở Console ---
    console.log(`Sản phẩm: ${product.productName}`);
    console.log('  discountPercentage từ API:', product.discountPercentage);
    console.log('  comparePrice từ API:', product.comparePrice);
    console.log('  price từ API:', product.price);
    console.log('  salePrice từ API:', product.salePrice);
    console.log('  discountPercent sau parse:', discountPercent);
    console.log('  originalPrice sau parse:', originalPrice);
    console.log('  finalPrice tính được:', finalPrice);
    console.log('  hasPromotion:', hasPromotion);

    const truncatedName = product.productName && product.productName.length > 50
        ? product.productName.substring(0, 47) + '...'
        : product.productName || 'Không có tên';

    const imageUrl = product.galleries && product.galleries[0]?.image
        ? `https://rubilia.store${product.galleries[0].image}`
        : 'https://via.placeholder.com/150';

    const salesCount = product.displayInfos?.[0]?.salesCount || 0;
    const rating = product.displayInfos?.[0]?.rating || 0;

    const renderStars = (rating) => {
        const stars = [];
        for (let i = 1; i <= 5; i++) {
            stars.push(<span key={i} className={i <= rating ? 'star filled' : 'star'}>★</span>);
        }
        return stars;
    };

    return (
        <div className="product-card-wrapper">
            <Link to={`/product/${product.id}`} style={{ textDecoration: 'none' }}>
                <div className="product-card">
                    <div className="product-card-image-wrapper">
                        <img src={imageUrl} className="product-card-image" alt={truncatedName}
                            onError={(e) => e.target.src = 'https://via.placeholder.com/150?text=No+Image'} />
                        
                        <div className="product-card-freeship">FREESHIP</div>
                        
                        {/* HIỆN % GIẢM GIÁ */}
                        {hasPromotion && (
                            <div className="product-card-discount">
                                -{Math.round(discountPercent)}%
                            </div>
                        )}
                    </div>

                    <div className="product-card-body">
                        <h5 className="product-card-title">{truncatedName}</h5>
                        
                        <div className="product-card-price-section">
                            {hasPromotion ? (
                                <>
                                    {/* Giá hiện tại (Màu đỏ) */}
                                    <p className="product-card-current-price">
                                        {finalPrice > 0 ? finalPrice.toLocaleString() + ' đ' : 'Liên hệ'}
                                    </p>
                                    {/* Giá gốc (Gạch ngang) - Chỉ hiện khi có giá gốc */}
                                    {originalPrice > 0 && (
                                        <p className="product-card-original-price">
                                            <del>{originalPrice.toLocaleString()} đ</del>
                                        </p>
                                    )}
                                </>
                            ) : (
                                <p className="product-card-current-price">
                                    {originalPrice > 0 ? originalPrice.toLocaleString() + ' đ' : 'Liên hệ'}
                                </p>
                            )}
                        </div>

                        <div className="product-card-rating-section">
                            {renderStars(rating)}
                            <span className="product-card-sales-count">Đã bán {salesCount}</span>
                        </div>
                    </div>
                </div>
            </Link>
        </div>
    );
};

export default ProductCard;