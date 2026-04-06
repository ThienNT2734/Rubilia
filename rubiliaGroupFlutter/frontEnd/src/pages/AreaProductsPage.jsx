import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import ProductCard from '../components/ProductCard';
import '../css/AreaProductsPage.css';

const AreaProductsPage = () => {
    const { area } = useParams();
    const [products, setProducts] = useState([]);
    const [error, setError] = useState('');

    const areaLabels = {
        flashsale: 'Flash Sale',
        trang_diem: 'Trang Điểm',
        cham_soc_da: 'Chăm Sóc Da',
        cham_soc_co_the: 'Chăm Sóc Cơ Thể',
        phu_kien: 'Phụ Kiện',
        mat_na: 'Mặt Nạ',
        deal_khung_chao_he: 'Deal Khủng Chào Hè',
    };

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await axios.get(`https://rubilia.store/api/products/display-area/${area}`);
                setProducts(response.data); // Bỏ giới hạn 5 sản phẩm
            } catch (err) {
                console.error(`Error fetching products for ${area}:`, err);
                setError('Không thể lấy danh sách sản phẩm.');
            }
        };
        fetchProducts();
    }, [area]);

    return (
        <div className="area-products-container">
            <h2 className="area-products-title">{areaLabels[area] || 'Khu Vực Sản Phẩm'}</h2>
            {error && <p className="area-products-error">{error}</p>}
            <div className="row area-products-grid">
                {products.length > 0 ? (
                    products.map(product => (
                        <div key={product.id} className="col-md-2-4 mb-3">
                            <ProductCard product={product} />
                        </div>
                    ))
                ) : (
                    <p className="area-products-empty">Chưa có sản phẩm nào trong khu vực này.</p>
                )}
            </div>
        </div>
    );
};

export default AreaProductsPage;