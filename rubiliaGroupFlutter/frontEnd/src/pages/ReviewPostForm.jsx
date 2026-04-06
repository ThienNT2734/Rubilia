import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import Image from '@tiptap/extension-image';
import TextAlign from '@tiptap/extension-text-align';
import FontFamily from '@tiptap/extension-font-family';
import TextStyle from '@tiptap/extension-text-style';
import '../css/ReviewPostForm.css';

const ReviewPostForm = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const staffId = '123e4567-e89b-12d3-a456-426614174000';

    const editingPost = location.state?.post || null;

    const [formData, setFormData] = useState({
        title: editingPost?.title || '',
        content: editingPost?.content || '',
        productId: editingPost?.product?.id || '',
        imageUrl: editingPost?.imageUrl || '',
    });
    const [products, setProducts] = useState([]);
    const [error, setError] = useState('');
    const [images, setImages] = useState([]);

    useEffect(() => {
        if (location.state?.editMode && !editingPost) {
            setError('Không tìm thấy dữ liệu bài viết để chỉnh sửa.');
        }
    }, [location.state, editingPost]);

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
        content: formData.content,
        onUpdate: ({ editor }) => {
            setFormData(prev => ({
                ...prev,
                content: editor.getHTML(),
            }));
        },
        editable: true,
    });

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await axios.get('https://rubilia.store/api/products');
                setProducts(response.data);
            } catch (err) {
                console.error('Lỗi lấy danh sách sản phẩm:', err);
                setError('Không thể lấy danh sách sản phẩm.');
            }
        };

        fetchProducts();
    }, []);

    useEffect(() => {
        if (editor && formData.content !== editor.getHTML()) {
            editor.commands.setContent(formData.content);
        }
    }, [editor, formData.content]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleImageUpload = async (e) => {
        const files = Array.from(e.target.files);
        const uploadPromises = files.map(file => {
            const formData = new FormData();
            formData.append('file', file);
            return axios.post('https://rubilia.store/api/files/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            }).then(response => response.data.url);
        });

        try {
            const uploadedImageUrls = await Promise.all(uploadPromises);
            setImages(prev => [...prev, ...uploadedImageUrls]);
            uploadedImageUrls.forEach(url => {
                editor.chain().focus().setImage({ src: url }).run();
            });
        } catch (err) {
            console.error('Lỗi tải ảnh lên:', err);
            setError('Không thể tải ảnh lên. Vui lòng kiểm tra endpoint /api/files/upload.');
        }
    };

    const handleRemoveImage = (index) => {
        setImages(prev => prev.filter((_, i) => i !== index));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const postData = {
                title: formData.title,
                content: formData.content,
                productId: formData.productId || null,
                imageUrl: formData.imageUrl || null,
            };

            console.log('Dữ liệu gửi đi:', JSON.stringify(postData, null, 2));

            const config = {
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
            };

            let response;
            if (editingPost) {
                response = await axios.put(
                    `https://rubilia.store/api/review-posts/${editingPost.id}?staffId=${staffId}`,
                    postData,
                    config
                );
                console.log('Phản hồi từ server (PUT):', response.data);
                alert('Cập nhật bài viết thành công!');
            } else {
                response = await axios.post(
                    `https://rubilia.store/api/review-posts?staffId=${staffId}`,
                    postData,
                    config
                );
                console.log('Phản hồi từ server (POST):', response.data);
                alert('Thêm bài viết thành công!');
            }

            navigate('/admin/dashboard');
        } catch (err) {
            console.error('Lỗi xử lý bài viết:', err.response?.data || err.message);
            console.error('Chi tiết lỗi:', err.response?.status, err.response?.headers);
            setError(err.response?.data?.message || 'Lỗi khi xử lý bài viết: ' + (err.response?.data?.error || err.message));
        }
    };

    const handleCancel = () => {
        navigate('/admin/dashboard');
    };

    if (error && location.state?.editMode && !editingPost) {
        return (
            <div className="review-post-form-container">
                <h2 className="review-post-form-title">Lỗi</h2>
                <p className="review-post-error">{error}</p>
                <button
                    type="button"
                    className="review-post-btn-secondary"
                    onClick={handleCancel}
                >
                    Quay lại
                </button>
            </div>
        );
    }

    return (
        <div className="review-post-form-container">
            <h2 className="review-post-form-title">
                {editingPost ? 'Sửa Bài Viết' : 'Thêm Bài Viết'}
            </h2>
            <div className="review-post-form-card">
                <form onSubmit={handleSubmit}>
                    <div className="review-post-form-group">
                        <label className="review-post-form-label">Tiêu đề</label>
                        <input
                            type="text"
                            name="title"
                            value={formData.title}
                            onChange={handleInputChange}
                            className="review-post-form-control"
                            required
                        />
                    </div>
                    <div className="review-post-form-group">
                        <label className="review-post-form-label">Sản phẩm</label>
                        <select
                            name="productId"
                            value={formData.productId}
                            onChange={handleInputChange}
                            className="review-post-form-control"
                            required
                        >
                            <option value="">Chọn sản phẩm</option>
                            {products.map(product => (
                                <option key={product.id} value={product.id}>
                                    {product.productName}
                                </option>
                            ))}
                        </select>
                    </div>
                    <div className="review-post-form-group">
                        <label className="review-post-form-label">URL Ảnh Đại Diện</label>
                        <input
                            type="url"
                            name="imageUrl"
                            value={formData.imageUrl}
                            onChange={handleInputChange}
                            className="review-post-form-control"
                            placeholder="Nhập đường dẫn ảnh (ví dụ: https://example.com/image.jpg)"
                        />
                    </div>
                    <div className="review-post-form-group">
                        <label className="review-post-form-label">Nội dung</label>
                        <div className="review-post-tiptap-editor">
                            <div className="review-post-tiptap-toolbar">
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
                                    className="review-post-tiptap-select"
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
                    <div className="review-post-form-group">
                        <label className="review-post-form-label">Tải ảnh lên (nếu cần)</label>
                        <input
                            type="file"
                            multiple
                            accept="image/*"
                            onChange={handleImageUpload}
                            className="review-post-form-control"
                        />
                        {images.length > 0 && (
                            <div className="review-post-image-preview">
                                {images.map((url, index) => (
                                    <div key={index} className="review-post-image-item">
                                        <img
                                            src={url}
                                            alt={`Uploaded ${index}`}
                                            style={{ width: '100px', margin: '5px' }}
                                        />
                                        <button
                                            type="button"
                                            className="review-post-image-remove-btn"
                                            onClick={() => handleRemoveImage(index)}
                                        >
                                            Xóa
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                    {error && <p className="review-post-error">{error}</p>}
                    <div className="review-post-actions">
                        <button type="submit" className="review-post-btn-primary">
                            {editingPost ? 'Cập Nhật' : 'Thêm'}
                        </button>
                        <button
                            type="button"
                            className="review-post-btn-secondary"
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

export default ReviewPostForm;
