import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import Image from '@tiptap/extension-image';
import TextAlign from '@tiptap/extension-text-align';
import FontFamily from '@tiptap/extension-font-family';
import TextStyle from '@tiptap/extension-text-style';
import { adaptProductDataForUpdate } from '../utils/ProductDataAdapter';
import '../css/ProductFormPage.css';

const ProductFormPage = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const staffId = '123e4567-e89b-12d3-a456-426614174000';
    const editingProduct = location.state?.product || null;
    const editMode = location.state?.editMode || false;

    const [productForm, setProductForm] = useState({
        productName: editingProduct?.productName || '',
        salePrice: editingProduct?.salePrice || '',
        comparePrice: editingProduct?.comparePrice || '',
        quantity: editingProduct?.quantity || '',
        shortDescription: editingProduct?.shortDescription || '',
        productDescription: editingProduct?.productDescription || '',
        images: [],
        displayAreas: editingProduct?.displayInfos?.map(info => info.displayArea) || [],
        salesCount: editingProduct?.displayInfos?.[0]?.salesCount || '0',
        rating: editingProduct?.displayInfos?.[0]?.rating || '0',
        selectedCategories: editingProduct?.productCategories?.map(pc => pc.category.id) || [],
    });
    const [existingImages, setExistingImages] = useState(editingProduct?.galleries || []);
    const [categories, setCategories] = useState([]);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const displayAreasOptions = [
        'flashsale',
        'trang_diem',
        'cham_soc_da',
        'cham_soc_co_the',
        'phu_kien',
        'mat_na',
        'deal_khung_chao_he',
    ];

    const editor = useEditor({
        extensions: [
            StarterKit,
            Image,
            TextAlign.configure({
                types: ['heading', 'paragraph'],
            }),
            FontFamily,
            TextStyle,
        ],
        content: productForm.productDescription,
        onUpdate: ({ editor }) => {
            setProductForm(prev => ({
                ...prev,
                productDescription: editor.getHTML(),
            }));
        },
        editable: true,
    });

    useEffect(() => {
        if (editMode && !editingProduct) {
            setError('Không tìm thấy dữ liệu sản phẩm để chỉnh sửa.');
        }
    }, [editMode, editingProduct]);

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/categories');
                setCategories(response.data);
            } catch (err) {
                console.error('Lỗi lấy danh sách danh mục:', err);
                setError('Không thể lấy danh sách danh mục.');
            }
        };
        fetchCategories();
    }, []);

    useEffect(() => {
        if (editor && productForm.productDescription !== editor.getHTML()) {
            editor.commands.setContent(productForm.productDescription);
        }
    }, [editor, productForm.productDescription]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setProductForm(prev => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleCategoryChange = (categoryId) => {
        setProductForm(prev => {
            const updatedCategories = prev.selectedCategories.includes(categoryId)
                ? prev.selectedCategories.filter(id => id !== categoryId)
                : [...prev.selectedCategories, categoryId];
            return { ...prev, selectedCategories: updatedCategories };
        });
    };

    const handleDisplayAreaChange = (area) => {
        setProductForm(prev => {
            const updatedAreas = prev.displayAreas.includes(area)
                ? prev.displayAreas.filter(a => a !== area)
                : [...prev.displayAreas, area];
            // Loại bỏ trùng lặp ngay tại đây
            return { ...prev, displayAreas: [...new Set(updatedAreas)] };
        });
    };

    const handleImageUpload = (e) => {
        const files = Array.from(e.target.files);
        const imagePromises = files.map(file => {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onloadend = () => resolve(reader.result);
                reader.onerror = reject;
                reader.readAsDataURL(file);
            });
        });

        Promise.all(imagePromises)
            .then(images => {
                setProductForm(prev => ({
                    ...prev,
                    images: [...prev.images, ...images],
                }));
            })
            .catch(err => {
                console.error('Lỗi upload hình ảnh:', err);
                setError('Không thể upload hình ảnh.');
            });
    };

    const handleRemoveImage = (index, isExisting = false) => {
        if (isExisting) {
            setExistingImages(prev => prev.filter((_, i) => i !== index));
        } else {
            setProductForm(prev => ({
                ...prev,
                images: prev.images.filter((_, i) => i !== index),
            }));
        }
    };

    const handleProductSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            if (!editingProduct && editMode) {
                throw new Error('Không tìm thấy dữ liệu sản phẩm để chỉnh sửa');
            }

            const allImages = Array.isArray(existingImages)
                ? [...existingImages.map(img => img.image).filter(img => img != null), ...productForm.images]
                : [...productForm.images];

            // Chuẩn hóa dữ liệu trước khi gửi
            const parsedSalePrice = parseFloat(productForm.salePrice) || 0;
            const parsedComparePrice = parseFloat(productForm.comparePrice) || 0;
            const parsedQuantity = parseInt(productForm.quantity) || 0;
            const parsedSalesCount = parseFloat(productForm.salesCount) || 0;
            let parsedRating = parseFloat(productForm.rating) || 0;

            if (parsedSalePrice < 0) throw new Error('Giá bán không hợp lệ');
            if (parsedComparePrice < 0) throw new Error('Giá so sánh không hợp lệ');
            if (parsedQuantity < 0) throw new Error('Số lượng không hợp lệ');
            if (parsedSalesCount < 0) throw new Error('Số lượng bán không hợp lệ');
            if (parsedRating < 1 || parsedRating > 5) parsedRating = 1; // Đặt mặc định nếu rating không hợp lệ

            // Chuẩn bị dữ liệu sản phẩm
            const productData = adaptProductDataForUpdate({
                ...editingProduct,
                productName: productForm.productName || '',
                salePrice: parsedSalePrice,
                comparePrice: parsedComparePrice,
                quantity: parsedQuantity,
                shortDescription: productForm.shortDescription || '',
                productDescription: productForm.productDescription || '',
                galleries: Array.isArray(existingImages) ? existingImages : [],
                displayInfos: Array.isArray(productForm.displayAreas)
                    ? productForm.displayAreas.map(area => ({ displayArea: area }))
                    : [],
                productCategories: Array.isArray(productForm.selectedCategories)
                    ? productForm.selectedCategories.map(id => ({ category: { id } }))
                    : [],
            }, {
                displayAreas: productForm.displayAreas.length > 0 ? productForm.displayAreas : ["default"],
                salesCount: parsedSalesCount,
                rating: parsedRating,
            });

            // Ghi đè các trường cần thiết
            productData.images = allImages.length > 0 ? allImages : ["/uploads/placeholder.jpg"];
            productData.idCategories = Array.isArray(productForm.selectedCategories)
                ? productForm.selectedCategories
                : [];
            productData.createdAt = editMode && editingProduct?.createdAt
                ? new Date(editingProduct.createdAt).toISOString()
                : new Date().toISOString();
            productData.updatedAt = new Date().toISOString();

            // Log dữ liệu trước khi gửi với đầy đủ các trường, sử dụng JSON.stringify để hiển thị toàn bộ
            console.log('Product data being sent to backend:', JSON.stringify({
                productName: productData.productName,
                salePrice: productData.salePrice,
                comparePrice: productData.comparePrice,
                quantity: productData.quantity,
                shortDescription: productData.shortDescription,
                productDescription: productData.productDescription,
                idCategories: productData.idCategories,
                images: productData.images,
                displayAreas: productData.displayAreas,
                salesCount: productData.salesCount,
                rating: productData.rating,
                createdAt: productData.createdAt,
                updatedAt: productData.updatedAt,
            }, null, 2));

            let response;
            if (editMode) {
                response = await axios.put(
                    `http://localhost:8080/api/products/${editingProduct.id}?staffId=${staffId}`,
                    productData
                );
                setSuccess('Cập nhật sản phẩm thành công!');
            } else {
                response = await axios.post(`http://localhost:8080/api/products?staffId=${staffId}`, productData);
                setSuccess('Thêm sản phẩm thành công!');
            }

            if (response.data && Array.isArray(response.data.galleries)) {
                setExistingImages(response.data.galleries);
            }

            setTimeout(() => navigate('/admin/dashboard'), 1000);
        } catch (err) {
            console.error('Lỗi xử lý sản phẩm:', err.response?.data || err.message);
            const errorMessage = err.response?.data?.message || err.response?.data?.error || err.message || 'Lỗi khi xử lý sản phẩm.';
            setError(errorMessage);
        }
    };

    const handleCancel = () => {
        navigate('/admin/dashboard');
    };

    if (error && editMode && !editingProduct) {
        return (
            <div className="product-form-container">
                <h2 className="product-form-title">Lỗi</h2>
                <p className="product-form-error">{error}</p>
                <button
                    type="button"
                    className="product-form-btn-secondary"
                    onClick={handleCancel}
                >
                    Quay lại
                </button>
            </div>
        );
    }

    return (
        <div className="product-form-container">
            <h2 className="product-form-title">
                {editMode ? 'Sửa Sản Phẩm' : 'Thêm Sản Phẩm'}
            </h2>
            {success && <p className="product-form-success">{success}</p>}
            <div className="product-form-card">
                <form onSubmit={handleProductSubmit}>
                    <div className="product-form-group">
                        <label className="product-form-label">Tên Sản Phẩm</label>
                        <input
                            type="text"
                            name="productName"
                            value={productForm.productName}
                            onChange={handleInputChange}
                            className="product-form-control"
                            required
                        />
                    </div>
                    <div className="product-form-group">
                        <label className="product-form-label">Giá Bán</label>
                        <input
                            type="number"
                            name="salePrice"
                            value={productForm.salePrice}
                            onChange={handleInputChange}
                            className="product-form-control"
                            required
                            min="0"
                            step="0.01"
                        />
                    </div>
                    <div className="product-form-group">
                        <label className="product-form-label">Giá So Sánh</label>
                        <input
                            type="number"
                            name="comparePrice"
                            value={productForm.comparePrice}
                            onChange={handleInputChange}
                            className="product-form-control"
                            min="0"
                            step="0.01"
                        />
                    </div>
                    <div className="product-form-group">
                        <label className="product-form-label">Số Lượng</label>
                        <input
                            type="number"
                            name="quantity"
                            value={productForm.quantity}
                            onChange={handleInputChange}
                            className="product-form-control"
                            required
                            min="0"
                        />
                    </div>
                    <div className="product-form-group">
                        <label className="product-form-label">Số Lượng Bán Ra (k)</label>
                        <input
                            type="number"
                            name="salesCount"
                            value={productForm.salesCount}
                            onChange={handleInputChange}
                            className="product-form-control"
                            step="0.1"
                            min="0"
                        />
                    </div>
                    <div className="product-form-group">
                        <label className="product-form-label">Số Sao (1-5)</label>
                        <input
                            type="number"
                            name="rating"
                            value={productForm.rating}
                            onChange={handleInputChange}
                            className="product-form-control"
                            step="0.1"
                            min="1"
                            max="5"
                        />
                    </div>
                    <div className="product-form-group">
                        <label className="product-form-label">Danh Mục</label>
                        <div className="product-form-checkbox-group">
                            {categories.map(category => (
                                <label key={category.id} className="product-form-checkbox-label">
                                    <input
                                        type="checkbox"
                                        checked={productForm.selectedCategories.includes(category.id)}
                                        onChange={() => handleCategoryChange(category.id)}
                                    />
                                    {category.categoryName}
                                </label>
                            ))}
                        </div>
                    </div>
                    <div className="product-form-group">
                        <label className="product-form-label">Mô Tả Ngắn</label>
                        <textarea
                            name="shortDescription"
                            value={productForm.shortDescription}
                            onChange={handleInputChange}
                            className="product-form-control"
                            rows="3"
                            required
                        />
                    </div>
                    <div className="product-form-group">
                        <label className="product-form-label">Mô Tả Đầy Đủ</label>
                        <div className="product-form-tiptap-editor">
                            <div className="product-form-tiptap-toolbar">
                                <button
                                    type="button"
                                    onClick={() => editor?.chain().focus().toggleBold().run()}
                                    className={editor?.isActive('bold') ? 'is-active' : ''}
                                >
                                    <strong>B</strong>
                                </button>
                                <button
                                    type="button"
                                    onClick={() => editor?.chain().focus().toggleItalic().run()}
                                    className={editor?.isActive('italic') ? 'is-active' : ''}
                                >
                                    <em>I</em>
                                </button>
                                <button
                                    type="button"
                                    onClick={() => editor?.chain().focus().toggleBulletList().run()}
                                    className={editor?.isActive('bulletList') ? 'is-active' : ''}
                                >
                                    • List
                                </button>
                                <button
                                    type="button"
                                    onClick={() => editor?.chain().focus().setTextAlign('left').run()}
                                    className={editor?.isActive('textAlign', { align: 'left' }) ? 'is-active' : ''}
                                >
                                    Left
                                </button>
                                <button
                                    type="button"
                                    onClick={() => editor?.chain().focus().setTextAlign('center').run()}
                                    className={editor?.isActive('textAlign', { align: 'center' }) ? 'is-active' : ''}
                                >
                                    Center
                                </button>
                                <button
                                    type="button"
                                    onClick={() => editor?.chain().focus().setTextAlign('right').run()}
                                    className={editor?.isActive('textAlign', { align: 'right' }) ? 'is-active' : ''}
                                >
                                    Right
                                </button>
                                <select
                                    onChange={(e) => editor?.chain().focus().setFontFamily(e.target.value).run()}
                                    className="product-form-tiptap-select"
                                >
                                    <option value="Arial">Arial</option>
                                    <option value="Times New Roman">Times New Roman</option>
                                    <option value="Courier New">Courier New</option>
                                </select>
                                <button
                                    type="button"
                                    onClick={() => {
                                        const url = prompt('Nhập URL hình ảnh:');
                                        if (url) {
                                            editor?.chain().focus().setImage({ src: url }).run();
                                        }
                                    }}
                                >
                                    Chèn Ảnh
                                </button>
                            </div>
                            <EditorContent editor={editor} />
                        </div>
                    </div>
                    <div className="product-form-group">
                        <label className="product-form-label">Khu Vực Hiển Thị</label>
                        <div className="product-form-checkbox-group">
                            {displayAreasOptions.map(area => (
                                <label key={area} className="product-form-checkbox-label">
                                    <input
                                        type="checkbox"
                                        checked={productForm.displayAreas.includes(area)}
                                        onChange={() => handleDisplayAreaChange(area)}
                                    />
                                    {area === 'flashsale' && 'Flash Sale'}
                                    {area === 'trang_diem' && 'Trang Điểm'}
                                    {area === 'cham_soc_da' && 'Chăm Sóc Da'}
                                    {area === 'cham_soc_co_the' && 'Chăm Sóc Cơ Thể'}
                                    {area === 'phu_kien' && 'Phụ Kiện'}
                                    {area === 'mat_na' && 'Mặt Nạ'}
                                    {area === 'deal_khung_chao_he' && 'Deal Khủng Chào Hè'}
                                </label>
                            ))}
                        </div>
                    </div>
                    <div className="product-form-group">
                        <label className="product-form-label">Hình Ảnh Hiện Có (Existing {existingImages.length})</label>
                        {existingImages.length > 0 && (
                            <div className="product-form-image-preview">
                                {existingImages.map((image, index) => (
                                    <div key={index} className="product-form-image-item">
                                        <img
                                            src={`http://localhost:8080${image.image}`}
                                            alt={`Existing ${index}`}
                                            style={{ width: '100px', margin: '5px' }}
                                            onError={(e) => {
                                                e.target.src = 'https://via.placeholder.com/150?text=Image+Not+Found';
                                                e.target.alt = 'Image not found';
                                            }}
                                        />
                                        <button
                                            type="button"
                                            className="product-form-image-remove-btn"
                                            onClick={() => handleRemoveImage(index, true)}
                                        >
                                            Xóa
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                    <div className="product-form-group">
                        <label className="product-form-label">Tải Hình Ảnh Lên (New {productForm.images.length})</label>
                        <input
                            type="file"
                            multiple
                            accept="image/*"
                            onChange={handleImageUpload}
                            className="product-form-control"
                        />
                        {productForm.images.length > 0 && (
                            <div className="product-form-image-preview">
                                {productForm.images.map((image, index) => (
                                    <div key={index} className="product-form-image-item">
                                        <img
                                            src={image}
                                            alt={`New ${index}`}
                                            style={{ width: '100px', margin: '5px' }}
                                            onError={(e) => {
                                                e.target.src = 'https://via.placeholder.com/150?text=Image+Not+Found';
                                                e.target.alt = 'Image not found';
                                            }}
                                        />
                                        <button
                                            type="button"
                                            className="product-form-image-remove-btn"
                                            onClick={() => handleRemoveImage(index)}
                                        >
                                            Xóa
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                    {error && <p className="product-form-error">{error}</p>}
                    <div className="product-form-actions">
                        <button type="submit" className="product-form-btn-primary">
                            {editMode ? 'Cập Nhật' : 'Thêm'}
                        </button>
                        <button
                            type="button"
                            className="product-form-btn-secondary"
                            onClick={handleCancel}
                        >
                            Hủy
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ProductFormPage;