
import React from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import AdminProtectedRoute from './components/AdminProtectedRoute';
import Home from './pages/Home';
import AdminLogin from './pages/AdminLogin';
import AdminDashboard from './pages/AdminDashboard';
import AdminDashboard2 from './pages/AdminDashboard2';
import PromotionsPage from './pages/PromotionsPage';
import ProductDetailPage from './pages/ProductDetailPage';
import Cart from './pages/Cart';
import UserProfile from './pages/UserProfile';
import Checkout from './pages/Checkout';
import AllProductsPage from './pages/AllProductsPage';
import ProductFormPage from './pages/ProductFormPage';
import ReviewPostForm from './pages/ReviewPostForm';
import CategoryForm from './pages/CategoryForm';
import SaleForm from './pages/SaleForm';
import OrderDetails from './pages/OrderDetails';
import AreaProductsPage from './pages/AreaProductsPage';
import SearchResultsPage from './pages/SearchResultsPage';
import BlogListPage from './pages/BlogListPage';
import BlogDetailPage from './pages/BlogDetailPage';
import ResetPassword from './pages/ResetPassword';

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            retry: 1,
            refetchOnWindowFocus: false,
            staleTime: 5 * 60 * 1000,
        },
    },
});

const AppContent = () => {
    const location = useLocation();
    const query = new URLSearchParams(location.search);
    const searchKeyword = query.get('search');

    // Kiểm tra nếu đường dẫn bắt đầu bằng /admin
    const isAdminRoute = location.pathname.startsWith('/admin');

    return (
        <>
            {!isAdminRoute && <Navbar />}
            <main className="flex-grow-1">
                <Routes>
                    <Route path="/" element={searchKeyword ? <SearchResultsPage /> : <Home />} />
                    <Route path="/product/:id" element={<ProductDetailPage />} />
                    <Route path="/cart" element={<Cart />} />
                    <Route path="/admin/login" element={<AdminLogin />} />
                    <Route path="/admin/dashboard" element={<AdminProtectedRoute><AdminDashboard /></AdminProtectedRoute>} />
                    <Route path="/admin/dashboard2" element={<AdminProtectedRoute><AdminDashboard2 /></AdminProtectedRoute>} />
                    <Route path="/admin/promotions" element={<AdminProtectedRoute><PromotionsPage /></AdminProtectedRoute>} />
                    <Route path="/admin/product-form" element={<AdminProtectedRoute><ProductFormPage /></AdminProtectedRoute>} />
                    <Route path="/admin/product/new" element={<AdminProtectedRoute><ProductFormPage /></AdminProtectedRoute>} />
                    <Route path="/admin/product/edit/:id" element={<AdminProtectedRoute><ProductFormPage /></AdminProtectedRoute>} />
                    <Route path="/admin/review-post-form" element={<AdminProtectedRoute><ReviewPostForm /></AdminProtectedRoute>} />
                    <Route path="/admin/category-form" element={<AdminProtectedRoute><CategoryForm /></AdminProtectedRoute>} />
                    <Route path="/admin/sale-form" element={<AdminProtectedRoute><SaleForm /></AdminProtectedRoute>} />
                    <Route path="/admin/order-details/:orderId" element={<AdminProtectedRoute><OrderDetails /></AdminProtectedRoute>} />
                    <Route path="/user/profile" element={<UserProfile />} />
                    <Route path="/checkout" element={<Checkout />} />
                    <Route path="/all-products" element={<AllProductsPage />} />
                    <Route path="/products/:area" element={<AreaProductsPage />} />
                    <Route path="/blog_lam_dep" element={<BlogListPage />} />
                    <Route path="/blog/:id" element={<BlogDetailPage />} />
                    <Route path="/reset-password" element={<ResetPassword />} />
                </Routes>
            </main>
            {!isAdminRoute && <Footer />}
        </>
    );
};

function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <Router>
                <div className="d-flex flex-column min-vh-100">
                    <AppContent />
                </div>
            </Router>
        </QueryClientProvider>
    );
}

export default App;
