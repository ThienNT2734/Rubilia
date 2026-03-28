import { getCurrentUserId } from './auth';

// Lấy giỏ hàng từ localStorage theo userId
export const getCart = () => {
    const userId = getCurrentUserId();
    const cartData = localStorage.getItem(`cart_${userId}`);
    return cartData ? JSON.parse(cartData) : [];
};

// Thêm sản phẩm vào giỏ hàng
export const addToCart = (product) => {
    const cart = getCart();
    const existingItem = cart.find(item => item.id === product.id);

    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({ ...product, quantity: 1 });
    }

    const userId = getCurrentUserId();
    localStorage.setItem(`cart_${userId}`, JSON.stringify(cart));
};

// Cập nhật số lượng sản phẩm
export const updateCartQuantity = (productId, action) => {
    const cart = getCart();
    let updatedCart;

    if (action === 'increase') {
        updatedCart = cart.map(item => {
            if (item.id === productId) {
                return { ...item, quantity: item.quantity + 1 };
            }
            return item;
        });
    } else if (action === 'decrease') {
        const item = cart.find(item => item.id === productId);
        if (item && item.quantity === 1) {
            // Nếu số lượng là 1 và giảm, xóa sản phẩm
            return removeFromCart(productId);
        } else {
            updatedCart = cart.map(item => {
                if (item.id === productId) {
                    return { ...item, quantity: item.quantity - 1 };
                }
                return item;
            });
        }
    } else {
        updatedCart = cart;
    }

    const userId = getCurrentUserId();
    localStorage.setItem(`cart_${userId}`, JSON.stringify(updatedCart));
    window.dispatchEvent(new Event('cartUpdated')); // Dispatch sự kiện để cập nhật giao diện
    return updatedCart;
};

// Xóa sản phẩm khỏi giỏ hàng
export const removeFromCart = (productId) => {
    const cart = getCart();
    const updatedCart = cart.filter(item => item.id !== productId);

    const userId = getCurrentUserId();
    localStorage.setItem(`cart_${userId}`, JSON.stringify(updatedCart));
    window.dispatchEvent(new Event('cartUpdated')); // Dispatch sự kiện để cập nhật giao diện
    return updatedCart;
};

// Lấy tổng số sản phẩm trong giỏ hàng
export const getCartItemCount = () => {
    const cart = getCart();
    return cart.reduce((sum, item) => sum + item.quantity, 0);
};

// Thêm hàm xóa toàn bộ giỏ hàng
export const clearCart = () => {
    const userId = getCurrentUserId();
    localStorage.removeItem(`cart_${userId}`);
    window.dispatchEvent(new Event('cartUpdated')); // Dispatch sự kiện để cập nhật giao diện
};