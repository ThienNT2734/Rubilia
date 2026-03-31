import React from 'react';

const Footer = () => {
    return (
        <footer className="footer bg-warning py-5">
            <div className="container">
                <div className="row">
                    {/* Thông tin công ty */}
                    <div className="col-md-4 mb-4">
                        <h5 className="footer-title">CÔNG TY TNHH RUBILIA VIỆT NAM</h5>
                        <p className="footer-text">
                            Giấy chứng nhận đăng ký doanh nghiệp số 0123456789, cấp ngày 15.03.2020, <br />
                            Nơi cấp Sở Kế hoạch và Đầu tư TP Hà Nội
                        </p>
                        <p className="footer-text">
                            Trụ sở chính: Tòa nhà Rubilia, 123 Đường Láng, Đống Đa, Hà Nội, VN
                        </p>
                        <p className="footer-text">
                            Hotline: <a href="tel:0123456789" className="footer-link">0123456789</a><br />
                            Điện thoại: <a href="tel:0367149735" className="footer-link">0367149735</a>
                        </p>
                    </div>

                    {/* Liên kết "Về chúng tôi" */}
                    <div className="col-md-4 mb-4">
                        <h5 className="footer-title">Về Rubilia</h5>
                        <ul className="footer-links list-unstyled">
                            <li><a href="#" className="footer-link">Giới thiệu về Rubilia</a></li>
                            <li><a href="#" className="footer-link">Blog Rubilia</a></li>
                            <li><a href="#" className="footer-link">Chương trình tích điểm</a></li>
                            <li><a href="#" className="footer-link">Hỗ trợ khách hàng</a></li>
                            <li><a href="#" className="footer-link">Hệ thống cửa hàng</a></li>
                            <li><a href="#" className="footer-link">Đối tác Rubilia</a></li>
                        </ul>
                    </div>

                    {/* Theo dõi và tải app */}
                    <div className="col-md-4 mb-4">
                        <h5 className="footer-title">Theo dõi Rubilia</h5>
                        <div className="social-icons mb-3">
                            <a href="https://facebook.com" target="_blank" rel="noopener noreferrer" className="social-icon">
                                <i className="fab fa-facebook-f"></i> Rubilia Facebook
                            </a>
                            <a href="https://instagram.com" target="_blank" rel="noopener noreferrer" className="social-icon">
                                <i className="fab fa-instagram"></i> Rubilia Instagram
                            </a>
                            <a href="https://tiktok.com" target="_blank" rel="noopener noreferrer" className="social-icon">
                                <i className="fab fa-tiktok"></i> Rubilia Vietnam Tiktok
                            </a>
                        </div>

                        <h5 className="footer-title mt-4">Tải App Rubilia</h5>
                        <div className="qr-code">
                            <img src={require('./images/qr.png')} alt="QR Code" className="img-fluid" />
                            <p className="footer-text mt-2">Quét mã QR để tải ứng dụng Rubilia</p>
                        </div>
                    </div>
                </div>

                {/* Phương thức thanh toán */}
                <div className="row mt-4">
                    <div className="col-12 text-center">
                        <h5 className="footer-title">Chấp nhận Phương thức thanh toán</h5>
                        <div className="payment-methods">
                            <img src={require('./images/vnpay.webp')} alt="VNPay" className="payment-icon mx-2" />
                            <img src={require('./images/momo.webp')} alt="Momo" className="payment-icon mx-2" />
                            <img src={require('./images/cash.webp')} alt="Cash" className="payment-icon mx-2" />
                        </div>
                    </div>
                </div>

                {/* Copyright */}
                <div className="row mt-4">
                    <div className="col-12 text-center">
                        <p className="footer-text mb-0">
                            © {new Date().getFullYear()} Rubilia. All rights reserved.
                        </p>
                    </div>
                </div>
            </div>
        </footer>
    );
};

export default Footer;