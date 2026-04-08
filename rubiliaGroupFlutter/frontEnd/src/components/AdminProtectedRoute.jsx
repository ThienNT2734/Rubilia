import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { isAdminAuthenticated } from '../utils/auth';

const AdminProtectedRoute = ({ children }) => {
    const location = useLocation();
    
    if (!isAdminAuthenticated()) {
        // Chuyển hướng về trang đăng nhập admin và lưu đường dẫn muốn truy cập
        return <Navigate to="/admin/login" state={{ from: location }} replace />;
    }
    
    return children;
};

export default AdminProtectedRoute;
