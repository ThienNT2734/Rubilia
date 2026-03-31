import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import ProductCard from '../components/ProductCard';
import '../css/AreaProductsPage.css';

const SearchResultsPage = () => {
    const location = useLocation();
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [error, setError] = useState('');
    const [keyword, setKeyword] = useState('');
    const [sortOption, setSortOption] = useState('default');
    const [priceRange, setPriceRange] = useState({ minPrice: '', maxPrice: '' });
    const [isFilterModalOpen, setIsFilterModalOpen] = useState(false);

    useEffect(() => {
        const query = new URLSearchParams(location.search);
        const searchKeyword = query.get('search') || '';
        setKeyword(searchKeyword);

        if (!searchKeyword.trim()) {
            setError('Vui lòng nhập từ khóa tìm kiếm.');
            setProducts([]);
            setFilteredProducts([]);
            return;
        }

        const fetchProducts = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/api/products/search?keyword=${encodeURIComponent(searchKeyword)}`);
                setProducts(response.data);
                setFilteredProducts(response.data);
            } catch (err) {
                console.error(`Error fetching products for keyword ${searchKeyword}:`, err);
                setError('Không thể lấy danh sách sản phẩm.');
                setProducts([]);
                setFilteredProducts([]);
            }
        };

        fetchProducts();
    }, [location.search]);

    useEffect(() => {
        let sortedProducts = [...products];

        // Sắp xếp sản phẩm
        if (sortOption === 'priceAsc') {
            sortedProducts.sort((a, b) => (a.salePrice || 0) - (b.salePrice || 0));
        } else if (sortOption === 'priceDesc') {
            sortedProducts.sort((a, b) => (b.salePrice || 0) - (a.salePrice || 0));
        } // 'default' giữ nguyên thứ tự gốc

        // Lọc theo khoảng giá
        const minPrice = parseFloat(priceRange.minPrice) || 0;
        const maxPrice = parseFloat(priceRange.maxPrice) || Infinity;
        sortedProducts = sortedProducts.filter(
            product => (product.salePrice || 0) >= minPrice && (product.salePrice || 0) <= maxPrice
        );

        setFilteredProducts(sortedProducts);
    }, [products, sortOption, priceRange]);

    const handleSortChange = (e) => {
        setSortOption(e.target.value);
    };

    const handlePriceFilterChange = (e) => {
        const { name, value } = e.target;
        setPriceRange(prev => ({ ...prev, [name]: value }));
    };

    const applyPriceFilter = (e) => {
        e.preventDefault();
        setIsFilterModalOpen(false);
    };

    const resetPriceFilter = () => {
        setPriceRange({ minPrice: '', maxPrice: '' });
        setIsFilterModalOpen(false);
    };

    return (
        <div className="area-products-container">
            <h2 className="area-products-title">Kết Quả Tìm Kiếm: {keyword || 'Không có từ khóa'}</h2>
            <div className="area-products-filter-section">
                <div className="area-products-sort">
                    <label htmlFor="sort" className="area-products-sort-label">Sắp xếp:</label>
                    <select
                        id="sort"
                        value={sortOption}
                        onChange={handleSortChange}
                        className="area-products-sort-select"
                    >
                        <option value="default">Bình thường</option>
                        <option value="priceAsc">Giá tăng dần</option>
                        <option value="priceDesc">Giá giảm dần</option>
                    </select>
                </div>
                <button
                    className="area-products-filter-btn"
                    onClick={() => setIsFilterModalOpen(true)}
                >
                    Bộ lọc giá
                </button>
            </div>
            {error && <p className="area-products-error">{error}</p>}
            <div className="row area-products-grid">
                {filteredProducts.length > 0 ? (
                    filteredProducts.map(product => (
                        <div key={product.id} className="col-md-2-4 mb-3">
                            <ProductCard product={product} />
                        </div>
                    ))
                ) : (
                    <p className="area-products-empty">Không tìm thấy sản phẩm nào.</p>
                )}
            </div>

            {/* Modal lọc giá */}
            {isFilterModalOpen && (
                <div className="area-products-filter-modal">
                    <div className="area-products-filter-modal-content">
                        <h3 className="area-products-filter-modal-title">Lọc theo giá</h3>
                        <form onSubmit={applyPriceFilter} className="area-products-filter-form">
                            <div className="area-products-filter-form-group">
                                <label htmlFor="minPrice" className="area-products-filter-label">Giá tối thiểu (₫):</label>
                                <input
                                    type="number"
                                    id="minPrice"
                                    name="minPrice"
                                    value={priceRange.minPrice}
                                    onChange={handlePriceFilterChange}
                                    className="area-products-filter-input"
                                    min="0"
                                    placeholder="0"
                                />
                            </div>
                            <div className="area-products-filter-form-group">
                                <label htmlFor="maxPrice" className="area-products-filter-label">Giá tối đa (₫):</label>
                                <input
                                    type="number"
                                    id="maxPrice"
                                    name="maxPrice"
                                    value={priceRange.maxPrice}
                                    onChange={handlePriceFilterChange}
                                    className="area-products-filter-input"
                                    min="0"
                                    placeholder="Vô cực"
                                />
                            </div>
                            <div className="area-products-filter-form-actions">
                                <button type="submit" className="area-products-filter-apply-btn">Áp dụng</button>
                                <button
                                    type="button"
                                    className="area-products-filter-reset-btn"
                                    onClick={resetPriceFilter}
                                >
                                    Xóa bộ lọc
                                </button>
                            </div>
                        </form>
                        <button
                            className="area-products-filter-modal-close"
                            onClick={() => setIsFilterModalOpen(false)}
                        >
                            <i className="fas fa-times"></i>
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SearchResultsPage;