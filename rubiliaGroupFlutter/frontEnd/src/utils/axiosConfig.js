import axios from 'axios';

const api = axios.create({
    baseURL: 'https://rubilia.store/api',
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true
});

// Interceptor để debug toàn bộ request
api.interceptors.request.use(
    (config) => {
        console.log('📤 === DEBUG AXIOS REQUEST ===');
        console.log('URL:', config.baseURL + config.url);
        console.log('Method:', config.method);
        console.log('Headers:', config.headers);
        console.log('With Credentials:', config.withCredentials);
        console.log('Cookies:', document.cookie);
        console.log('Data:', config.data);
        delete config.headers.Authorization;
        return config;
    },
    (error) => Promise.reject(error)
);

// Interceptor để debug toàn bộ response
api.interceptors.response.use(
    (response) => {
        console.log('📥 === DEBUG AXIOS RESPONSE SUCCESS ===');
        console.log('Status:', response.status);
        console.log('Headers:', response.headers);
        console.log('Data:', response.data);
        return response;
    },
    (error) => {
        console.error('❌ === DEBUG AXIOS RESPONSE ERROR ===');
        console.error('Status:', error.response?.status);
        console.error('Status Text:', error.response?.statusText);
        console.error('Headers:', error.response?.headers);
        console.error('Full Data:', error.response?.data);
        console.error('Stack:', error.stack);
        return Promise.reject(error);
    }
);

// Interceptor để xử lý response lỗi
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401 || error.response?.status === 403) {
            console.error('❌ Authentication failed - redirect to login');
            // Có thể tự động redirect đến trang đăng nhập ở đây
        }
        return Promise.reject(error);
    }
);

export default api;
