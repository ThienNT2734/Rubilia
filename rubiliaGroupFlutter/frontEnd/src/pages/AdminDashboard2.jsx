
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axios from 'axios';
import { isAuthenticated } from '../utils/auth';
import '../css/AdminDashboard.css';

const AdminDashboard2 = () => {
    const [activeTab, setActiveTab] = useState('comments');
    const [toast, setToast] = useState({ message: '', type: '', visible: false });
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    useEffect(() => {
        if (!isAuthenticated()) {
            navigate('/admin/login');
        }
    }, [navigate]);

    const showToast = (message, type) => {
        setToast({ message, type, visible: true });
        setTimeout(() => setToast({ message: '', type: '', visible: false }), 3000);
    };

    const parseCreatedAt = (createdAtArray) => {
        if (!Array.isArray(createdAtArray) || createdAtArray.length < 7) {
            return null;
        }
        const [year, month, day, hour, minute, second, nano] = createdAtArray;
        return new Date(year, month - 1, day, hour, minute, second, Math.floor(nano / 1000000));
    };

    const fetchComments = async () => {
        const response = await axios.get('https://rubilia.store/api/comments/all');
        if (response.status !== 200) {
            throw new Error('Phản hồi không thành công: ' + response.status);
        }
        return Array.isArray(response.data) ? response.data : [];
    };

    const fetchRatings = async () => {
        const response = await axios.get('https://rubilia.store/api/ratings/all');
        if (response.status !== 200) {
            throw new Error('Phản hồi không thành công: ' + response.status);
        }
        return Array.isArray(response.data) ? response.data : [];
    };

    const { data: commentsData, isLoading: isLoadingComments, error: commentsError } = useQuery({
        queryKey: ['comments'],
        queryFn: fetchComments,
        enabled: isAuthenticated(),
    });

    const { data: ratingsData, isLoading: isLoadingRatings, error: ratingsError } = useQuery({
        queryKey: ['ratings'],
        queryFn: fetchRatings,
        enabled: isAuthenticated(),
    });

    const comments = Array.isArray(commentsData) ? commentsData : [];
    const ratings = Array.isArray(ratingsData) ? ratingsData : [];

    const approveCommentMutation = useMutation({
        mutationFn: (commentId) => axios.put(`https://rubilia.store/api/comments/${commentId}/approve`),
        onSuccess: () => {
            showToast('Duyệt bình luận thành công!', 'success');
            queryClient.invalidateQueries(['comments']);
        },
        onError: (err) => {
            console.error('Lỗi duyệt bình luận:', err);
            showToast(err.response?.data?.message || err.message, 'error');
        },
    });

    const deleteCommentMutation = useMutation({
        mutationFn: (commentId) => axios.delete(`https://rubilia.store/api/comments/${commentId}`),
        onSuccess: () => {
            showToast('Xóa bình luận thành công!', 'success');
            queryClient.invalidateQueries(['comments']);
        },
        onError: (err) => {
            console.error('Lỗi xóa bình luận:', err);
            showToast(err.response?.data?.message || err.message, 'error');
        },
    });

    const approveRatingMutation = useMutation({
        mutationFn: (ratingId) => axios.put(`https://rubilia.store/api/ratings/${ratingId}/approve`),
        onSuccess: () => {
            showToast('Duyệt đánh giá thành công!', 'success');
            queryClient.invalidateQueries(['ratings']);
        },
        onError: (err) => {
            console.error('Lỗi duyệt đánh giá:', err);
            showToast(err.response?.data?.message || err.message, 'error');
        },
    });

    const deleteRatingMutation = useMutation({
        mutationFn: (ratingId) => axios.delete(`https://rubilia.store/api/ratings/${ratingId}`),
        onSuccess: () => {
            showToast('Xóa đánh giá thành công!', 'success');
            queryClient.invalidateQueries(['ratings']);
        },
        onError: (err) => {
            console.error('Lỗi xóa đánh giá:', err);
            showToast(err.response?.data?.message || err.message, 'error');
        },
    });

    const handleApproveComment = (commentId) => {
        approveCommentMutation.mutate(commentId);
    };

    const handleDeleteComment = (commentId) => {
        if (window.confirm('Bạn có chắc muốn xóa bình luận này?')) {
            deleteCommentMutation.mutate(commentId);
        }
    };

    const handleApproveRating = (ratingId) => {
        approveRatingMutation.mutate(ratingId);
    };

    const handleDeleteRating = (ratingId) => {
        if (window.confirm('Bạn có chắc muốn xóa đánh giá này?')) {
            deleteRatingMutation.mutate(ratingId);
        }
    };

    const getEmailPrefix = (email) => {
        if (!email) return 'Không có';
        return email.split('@')[0];
    };

    if (!isAuthenticated()) {
        return null;
    }

    return (
        <div className="admin-dashboard-container">
            {toast.visible && (
                <div className={`admin-dashboard-toast admin-dashboard-toast-${toast.type}`}>
                    {toast.message}
                </div>
            )}
            <div className="admin-dashboard-sidebar">
                <div className="admin-dashboard-header">
                    <h2 className="admin-dashboard-title">Đánh Giá & Bình Luận</h2>
                </div>
                <div className="admin-dashboard-tabs">
                    <button
                        className={`admin-dashboard-tab-btn ${activeTab === 'comments' ? 'active' : ''}`}
                        onClick={() => setActiveTab('comments')}
                    >
                        <span className="tab-icon">💬</span> Bình Luận
                    </button>
                    <button
                        className={`admin-dashboard-tab-btn ${activeTab === 'ratings' ? 'active' : ''}`}
                        onClick={() => setActiveTab('ratings')}
                    >
                        <span className="tab-icon">⭐</span> Đánh Giá
                    </button>
                </div>
                <button
                    className="admin-dashboard-logout-btn"
                    onClick={() => navigate('/admin/dashboard')}
                >
                    <span className="tab-icon">🔙</span> Trở Về
                </button>
            </div>
            <div className="admin-dashboard-content">
                {activeTab === 'comments' && (
                    <div className="admin-dashboard-tab-content">
                        <h3>Quản Lý Bình Luận</h3>
                        {commentsError && showToast(commentsError.message, 'error')}
                        {isLoadingComments ? (
                            <div className="admin-dashboard-loading">
                                <div className="admin-dashboard-loading-spinner"></div>
                                Đang tải...
                            </div>
                        ) : comments.length > 0 ? (
                            <div className="admin-dashboard-table-container">
                                <table className="admin-dashboard-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Người Dùng</th>
                                            <th>Sản Phẩm</th>
                                            <th>Nội Dung</th>
                                            <th>Ngày Gửi</th>
                                            <th>Trạng Thái</th>
                                            <th>Hành Động</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {comments.map(comment => {
                                            const createdAtDate = parseCreatedAt(comment.createdAt);
                                            return (
                                                <tr key={comment.id}>
                                                    <td>{comment.id}</td>
                                                    <td>{getEmailPrefix(comment.email)}</td>
                                                    <td>{comment.product?.productName || 'Không có'}</td>
                                                    <td>{comment.content || 'Không có'}</td>
                                                    <td>{createdAtDate ? createdAtDate.toLocaleString() : 'Không có'}</td>
                                                    <td>{comment.status || 'Không có'}</td>
                                                    <td className="actions">
                                                        {comment.status === 'PENDING' && (
                                                            <button
                                                                className="edit-btn"
                                                                onClick={() => handleApproveComment(comment.id)}
                                                                title="Duyệt bình luận"
                                                                disabled={approveCommentMutation.isLoading}
                                                            >
                                                                Duyệt
                                                            </button>
                                                        )}
                                                        <button
                                                            className="delete-btn"
                                                            onClick={() => handleDeleteComment(comment.id)}
                                                            title="Xóa bình luận"
                                                            disabled={deleteCommentMutation.isLoading}
                                                        >
                                                            Xóa
                                                        </button>
                                                    </td>
                                                </tr>
                                            );
                                        })}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <p className="admin-dashboard-empty">Chưa có bình luận nào.</p>
                        )}
                    </div>
                )}

                {activeTab === 'ratings' && (
                    <div className="admin-dashboard-tab-content">
                        <h3>Quản Lý Đánh Giá</h3>
                        {ratingsError && showToast(ratingsError.message, 'error')}
                        {isLoadingRatings ? (
                            <div className="admin-dashboard-loading">
                                <div className="admin-dashboard-loading-spinner"></div>
                                Đang tải...
                            </div>
                        ) : ratings.length > 0 ? (
                            <div className="admin-dashboard-table-container">
                                <table className="admin-dashboard-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Người Dùng</th>
                                            <th>Sản Phẩm</th>
                                            <th>Điểm Đánh Giá</th>
                                            <th>Ngày Gửi</th>
                                            <th>Trạng Thái</th>
                                            <th>Hành Động</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {ratings.map(rating => {
                                            const createdAtDate = parseCreatedAt(rating.createdAt);
                                            return (
                                                <tr key={rating.id}>
                                                    <td>{rating.id}</td>
                                                    <td>{getEmailPrefix(rating.email)}</td>
                                                    <td>{rating.product?.productName || 'Không có'}</td>
                                                    <td>{rating.rating ? `${rating.rating} sao` : 'Không có'}</td>
                                                    <td>{createdAtDate ? createdAtDate.toLocaleString() : 'Không có'}</td>
                                                    <td>{rating.status || 'Không có'}</td>
                                                    <td className="actions">
                                                        {rating.status === 'PENDING' && (
                                                            <button
                                                                className="edit-btn"
                                                                onClick={() => handleApproveRating(rating.id)}
                                                                title="Duyệt đánh giá"
                                                                disabled={approveRatingMutation.isLoading}
                                                            >
                                                                Duyệt
                                                            </button>
                                                        )}
                                                        <button
                                                            className="delete-btn"
                                                            onClick={() => handleDeleteRating(rating.id)}
                                                            title="Xóa đánh giá"
                                                            disabled={deleteRatingMutation.isLoading}
                                                        >
                                                            Xóa
                                                        </button>
                                                    </td>
                                                </tr>
                                            );
                                        })}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <p className="admin-dashboard-empty">Chưa có đánh giá nào.</p>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default AdminDashboard2;
