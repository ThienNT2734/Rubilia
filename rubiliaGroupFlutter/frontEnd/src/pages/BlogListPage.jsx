import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import '../css/BlogListPage.css';

const BlogListPage = () => {
    const [posts, setPosts] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const response = await axios.get('https://rubilia.store/api/review-posts');
                setPosts(response.data);
            } catch (err) {
                console.error('Error fetching blog posts:', err);
                setError('Không thể lấy danh sách bài viết.');
            }
        };
        fetchPosts();
    }, []);

    const stripHtml = (html) => {
        const div = document.createElement('div');
        div.innerHTML = html;
        return div.textContent || div.innerText || '';
    };

    return (
        <div className="blog-list-container">
            <h2 className="blog-list-title">Blog Làm Đẹp</h2>
            {error && <p className="blog-list-error">{error}</p>}
            <div className="row blog-list-grid">
                {posts.length > 0 ? (
                    posts.map(post => (
                        <div key={post.id} className="col-md-4 mb-4">
                            <Link to={`/blog/${post.id}`}>
                                <div className="blog-card">
                                    <div className="blog-card-image-wrapper">
                                        <img
                                            src={post.imageUrl || 'https://via.placeholder.com/300?text=Blog+Image'}
                                            alt={stripHtml(post.title)}
                                            className="blog-card-image"
                                            onError={(e) => {
                                                e.target.src = 'https://via.placeholder.com/300?text=Image+Not+Found';
                                                e.target.alt = 'Image not found';
                                            }}
                                        />
                                    </div>
                                    <div className="blog-card-body">
                                        <h5 className="blog-card-title">{stripHtml(post.title) || 'Không có tiêu đề'}</h5>
                                        <p className="blog-card-date">{new Date(post.createdAt).toLocaleDateString('vi-VN') || 'Không có ngày đăng'}</p>
                                        <p className="blog-card-excerpt">{post.content?.substring(0, 100) + '...' || 'Không có tóm tắt'}</p>
                                    </div>
                                </div>
                            </Link>
                        </div>
                    ))
                ) : (
                    <p className="blog-list-empty">Chưa có bài viết nào.</p>
                )}
            </div>
        </div>
    );
};

export default BlogListPage;