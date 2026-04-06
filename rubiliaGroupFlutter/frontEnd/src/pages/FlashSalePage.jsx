import React, { useEffect, useState } from 'react';
import axios from 'axios';
import ProductCard from '../components/ProductCard';

const FlashSalePage = () => {
    const [flashSaleProducts, setFlashSaleProducts] = useState([]);
    const [countdown, setCountdown] = useState({ days: 0, hours: 0, minutes: 0, seconds: 0 });

    useEffect(() => {
        axios.get('http://localhost:8080/api/sales')
            .then(response => {
                console.log('Flash Sale products:', response.data);
                setFlashSaleProducts(response.data);
            })
            .catch(error => console.error('Error fetching flash sale products:', error));
    }, []);

    useEffect(() => {
        if (flashSaleProducts.length > 0) {
            const endDate = new Date(flashSaleProducts[0].endDate).getTime();
            const interval = setInterval(() => {
                const now = new Date().getTime();
                const distance = endDate - now;

                if (distance <= 0) {
                    clearInterval(interval);
                    setCountdown({ days: 0, hours: 0, minutes: 0, seconds: 0 });
                    return;
                }

                const days = Math.floor(distance / (1000 * 60 * 60 * 24));
                const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((distance % (1000 * 60)) / 1000);

                setCountdown({ days, hours, minutes, seconds });
            }, 1000);

            return () => clearInterval(interval);
        }
    }, [flashSaleProducts]);

    return (
        <div className="flash-sale-page-container">
            <h2 className="flash-sale-page-title">Flash Sale</h2>
            {flashSaleProducts.length > 0 && (
                <div className="countdown-timer">
                    <div className="countdown-unit">
                        <span>{countdown.days}</span>
                        Ngày
                    </div>
                    <div className="countdown-unit">
                        <span>{countdown.hours}</span>
                        Giờ
                    </div>
                    <div className="countdown-unit">
                        <span>{countdown.minutes}</span>
                        Phút
                    </div>
                    <div className="countdown-unit">
                        <span>{countdown.seconds}</span>
                        Giây
                    </div>
                </div>
            )}
            <div className="row">
                {flashSaleProducts.length > 0 ? (
                    flashSaleProducts.map(sale => (
                        <ProductCard key={sale.id} product={sale.product} />
                    ))
                ) : (
                    <p>Không có sản phẩm Flash Sale.</p>
                )}
            </div>
        </div>
    );
};

export default FlashSalePage;