import React, { useState, useEffect } from 'react';
import api from '../utils/axiosConfig';
import '../css/NotificationBell.css';

const NotificationBell = ({ customerId }) => {
    const [notifications, setNotifications] = useState([]);
    const [unseenCount, setUnseenCount] = useState(0);
    const [isOpen, setIsOpen] = useState(false);

    useEffect(() => {
        if (customerId) {
            loadNotifications();
            const interval = setInterval(loadUnseenCount, 30000);
            return () => clearInterval(interval);
        }
    }, [customerId]);

    const loadNotifications = async () => {
        try {
            const res = await api.get(`/notifications/customer/${customerId}`);
            setNotifications(res.data);
        } catch (e) {
            console.error('Load notifications error', e);
        }
    };

    const loadUnseenCount = async () => {
        try {
            const res = await api.get(`/notifications/customer/${customerId}/count-unseen`);
            setUnseenCount(res.data);
        } catch (e) {
            console.error('Count unseen error', e);
        }
    };

    const markAsSeen = async (notificationId) => {
        try {
            await api.put(`/notifications/${notificationId}/seen`);
            setUnseenCount(prev => Math.max(0, prev - 1));
            setNotifications(prev => prev.map(n =>
                n.id === notificationId ? { ...n, isSeen: true } : n
            ));
        } catch (e) {
            console.error('Mark seen error', e);
        }
    };

    const toggleDropdown = () => {
        setIsOpen(!isOpen);
        if (!isOpen) {
            loadNotifications();
        }
    };

    return (
        <div className="notification-bell-container">
            <div className="notification-bell" onClick={toggleDropdown}>
                <i className="fas fa-bell"></i>
                {unseenCount > 0 && (
                    <span className="notification-badge">{unseenCount}</span>
                )}
            </div>

            {isOpen && (
                <div className="notification-dropdown">
                    <div className="notification-header">
                        <h5>Thông báo</h5>
                    </div>
                    <div className="notification-list">
                        {notifications.length === 0 ? (
                            <div className="notification-empty">Không có thông báo</div>
                        ) : (
                            notifications.map(notification => (
                                <div
                                    key={notification.id}
                                    className={`notification-item ${!notification.isSeen ? 'unseen' : ''}`}
                                    onClick={() => markAsSeen(notification.id)}
                                >
                                    {notification.imageUrl && (
                                        <img src={notification.imageUrl} className="notification-image" alt="" />
                                    )}
                                    <div className="notification-content">
                                        <div className="notification-title">{notification.title}</div>
                                        <div className="notification-text">{notification.content}</div>
                                        <div className="notification-time">
                                            {new Date(notification.createdAt).toLocaleDateString('vi-VN')}
                                        </div>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default NotificationBell;
