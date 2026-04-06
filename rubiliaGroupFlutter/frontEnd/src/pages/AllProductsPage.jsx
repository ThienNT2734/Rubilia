import React, { useEffect, useState } from 'react';
import axios from 'axios';
import ProductCard from '../components/ProductCard';

const AllProductsPage = () => {
    const [products, setProducts] = useState([]);

    useEffect(() => {
        axios.get('https://rubilia.store/api/products')
            .then(response => {
                console.log('Total products:', response.data);
                setProducts(response.data);
            })
            .catch(error => console.error('Error fetching products:', error));
    }, []);

    return (
        <div className="all-products-container">
            <h2 className="all-products-title">Tất Cả Sản Phẩm</h2>
            <div className="row">
                {products.length > 0 ? (
                    products.map(product => (
                        <ProductCard key={product.id} product={product} />
                    ))
                ) : (
                    <p>Không có sản phẩm nào để hiển thị.</p>
                )}
            </div>
        </div>
    );
};

export default AllProductsPage;