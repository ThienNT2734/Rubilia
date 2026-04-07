import axios from 'axios';

// Giả lập userId (sẽ thay bằng hệ thống đăng nhập thật sau)
export const getCurrentUserId = () => {
    let userId = localStorage.getItem('currentUserId');
    if (!userId) {
        userId = 'user_' + Math.random().toString(36).substr(2, 9);
        localStorage.setItem('currentUserId', userId);
    }
    return userId;
};

// Hàm để chuyển đổi tài khoản (giả lập đăng xuất và đăng nhập tài khoản khác)
export const switchUser = () => {
    localStorage.removeItem('currentUserId');
    return getCurrentUserId();
};

// Hàm đăng nhập và lưu trạng thái cho admin (staff)
export const login = (staffData) => {
    localStorage.setItem('staff', JSON.stringify(staffData));
    localStorage.setItem('currentUserId', staffData.id || 'staff_' + Math.random().toString(36).substr(2, 9));
};

// Hàm đăng nhập cho khách hàng (customer)
export const loginCustomer = async (email, password) => {
    try {
        const response = await axios.post('https://rubilia.store/api/customers/login', { user_name: email, password_hash: password });
        const customer = response.data;
        if (!customer.email) {
            console.error('Customer data missing email:', customer);
            throw new Error('Invalid customer data: missing email');
        }
        localStorage.setItem('customer', JSON.stringify(customer));
        localStorage.setItem('currentUserId', customer.id || 'customer_' + Math.random().toString(36).substr(2, 9));
        return customer;
    } catch (error) {
        console.error('Login error:', error.response?.data || error.message);
        throw error;
    }
};

// Hàm đăng nhập bằng Google
export const loginGoogle = async () => {
    try {
        // Mở popup để đăng nhập bằng Google
        const width = 600;
        const height = 600;
        const left = (window.screen.width - width) / 2;
        const top = (window.screen.height - height) / 2;
        const url = `https://rubilia.store/oauth2/authorization/google?redirect_uri=https://rubilia.store/login/oauth2/code/google`;
        window.open(url, 'oauth2', `width=${width},height=${height},top=${top},left=${left}`);

        // Trả về một Promise để chờ kết quả từ popup
        return new Promise((resolve, reject) => {
            const handleMessage = (event) => {
                if (event.origin !== 'https://rubilia.store') return;
                const { status, data, error } = event.data;
                if (status === 'success') {
                    if (!data.email) {
                        console.error('Google login data missing email:', data);
                        reject(new Error('Invalid customer data: missing email'));
                        return;
                    }
                    localStorage.setItem('customer', JSON.stringify(data));
                    localStorage.setItem('currentUserId', data.id || 'customer_' + Math.random().toString(36).substr(2, 9));
                    resolve(data);
                } else if (status === 'info_required') {
                    resolve({ status, data });
                } else {
                    reject(new Error(error || 'Đăng nhập Google thất bại'));
                }
                window.removeEventListener('message', handleMessage);
            };
            window.addEventListener('message', handleMessage);
        });
    } catch (error) {
        console.error('Google login error:', error.response?.data || error.message);
        throw error;
    }
};

// Hàm đăng ký OAuth2 khi thiếu thông tin
export const registerOAuth2 = async (email, firstName, lastName) => {
    try {
        const response = await axios.post('https://rubilia.store/api/customers/oauth2/register', {
            email,
            firstName,
            lastName
        });
        const customer = response.data;
        if (!customer.email) {
            console.error('OAuth2 register data missing email:', customer);
            throw new Error('Invalid customer data: missing email');
        }
        localStorage.setItem('customer', JSON.stringify(customer));
        localStorage.setItem('currentUserId', customer.id || 'customer_' + Math.random().toString(36).substr(2, 9));
        return customer;
    } catch (error) {
        console.error('OAuth2 register error:', error.response?.data || error.message);
        throw error;
    }
};

// Hàm kiểm tra trạng thái đăng nhập (kiểm tra cả staff và customer)
export const isAuthenticated = () => {
    const staff = localStorage.getItem('staff');
    const customer = localStorage.getItem('customer');
    const isAuth = !!(staff || customer);
    if (!isAuth) {
        console.warn('No authenticated user found in localStorage');
    }
    return isAuth;
};

// Hàm đăng xuất
export const logout = () => {
    localStorage.removeItem('staff');
    localStorage.removeItem('customer');
    localStorage.removeItem('currentUserId');
    window.dispatchEvent(new Event('storage'));
};

// Lấy thông tin staff hiện tại
export const getCurrentStaff = () => {
    const staffData = localStorage.getItem('staff');
    return staffData ? JSON.parse(staffData) : null;
};

// Lấy thông tin customer hiện tại
export const getCurrentCustomer = () => {
    const customerData = localStorage.getItem('customer');
    return customerData ? JSON.parse(customerData) : null;
};

// Lấy email của người dùng hiện tại (staff hoặc customer)
export const getCurrentUserEmail = () => {
    const staff = getCurrentStaff();
    const customer = getCurrentCustomer();
    if (staff && staff.email) {
        return staff.email;
    }
    if (customer && customer.email) {
        return customer.email;
    }
    console.error('No email found for authenticated user. Staff:', !!staff, 'Customer:', !!customer);
    return null;
};