import React from 'react';

const ProductDetail = ({ product }) => {
    if (!product) return <div>Sản phẩm không tồn tại</div>;

    const imageUrl = product.galleries && product.galleries[0]?.image
        ? `https://rubilia.store${product.galleries[0].image}`
        : 'https://via.placeholder.com/150';

    return (
        <div className="container mt-5">
            <div className="row">
                <div className="col-md-6">
                    <img
                        src={imageUrl}
                        alt={product.productName || 'Sản phẩm'}
                        className="img-fluid"
                        onError={(e) => {
                            e.target.src = 'https://via.placeholder.com/150?text=Image+Not+Found';
                            e.target.alt = 'Image not found';
                        }}
                    />
                </div>
                <div className="col-md-6">
                    <h2>{product.productName || 'Không có tên'}</h2>
                    <p>{product.shortDescription || 'Không có mô tả'}</p>
                    <p>Giá: {product.salePrice ? product.salePrice.toLocaleString() : '0'} đ</p>
                    <button className="btn btn-success">Thêm vào giỏ hàng</button>
                </div>
            </div>
        </div>
    );
};

export default ProductDetail;