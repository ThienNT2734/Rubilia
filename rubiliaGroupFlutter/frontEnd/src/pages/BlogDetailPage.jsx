import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';
import '../css/BlogDetailPage.css';

const BlogDetailPage = () => {
    const { id } = useParams();
    const [post, setPost] = useState(null);
    const [author, setAuthor] = useState(null);
    const [relatedPosts, setRelatedPosts] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchPost = async () => {
            try {
                const response = await axios.get(`https://rubilia.store/api/review-posts/${id}`);
                setPost(response.data);

                // Lấy thông tin tác giả nếu createdBy tồn tại
                if (response.data.createdBy) {
                    try {
                        const authorResponse = await axios.get(`https://rubilia.store/api/staff/${response.data.createdBy}`);
                        setAuthor(authorResponse.data);
                    } catch (authorErr) {
                        console.error('Error fetching author:', authorErr);
                        setAuthor(null);
                    }
                } else {
                    setAuthor(null);
                }

                // Lấy bài viết liên quan
                const relatedResponse = await axios.get('https://rubilia.store/api/review-posts');
                const related = relatedResponse.data
                    .filter(p => p.id !== id)
                    .slice(0, 3);
                setRelatedPosts(related);
            } catch (err) {
                console.error('Error fetching blog post:', err);
                setError('Không thể lấy bài viết.');
            }
        };
        fetchPost();
    }, [id]);

    if (error) return <div className="container my-5"><p>{error}</p></div>;
    if (!post) return <div className="container my-5"><p>Loading...</p></div>;

    return (
        <div className="blog-detail-container">
            <h1 className="blog-detail-title">{post.title || 'Không có tiêu đề'}</h1>
            <div className="blog-detail-meta">
                <p className="blog-detail-date">Ngày đăng: {new Date(post.createdAt).toLocaleDateString('vi-VN')}</p>
                <p className="blog-detail-author">Tác giả: {author ? `${author.firstName} ${author.lastName}` : 'Không có thông tin'}</p>
            </div>
            {/* Bỏ trường image vì review_posts không có cột này */}
            <div
                className="blog-detail-content"
                dangerouslySetInnerHTML={{ __html: post.content || 'Không có nội dung' }}
            />

            {relatedPosts.length > 0 && (
                <div className="blog-related-posts">
                    <h3 className="blog-related-title">Bài Viết Liên Quan</h3>
                    <div className="row">
                        {relatedPosts.map(relatedPost => (
                            <div key={relatedPost.id} className="col-md-4 mb-4">
                                <div className="blog-card">
                                    <div className="blog-card-image-wrapper">
                                        <img
                                            src={'https://via.placeholder.com/300?text=Blog+Image'}
                                            alt={relatedPost.title}
                                            className="blog-card-image"
                                            onError={(e) => {
                                                e.target.src = 'https://via.placeholder.com/300?text=Image+Not+Found';
                                                e.target.alt = 'Image not found';
                                            }}
                                        />
                                    </div>
                                    <div className="blog-card-body">
                                        <h5 className="blog-card-title">{relatedPost.title || 'Không có tiêu đề'}</h5>
                                        <p className="blog-card-date">{new Date(relatedPost.createdAt).toLocaleDateString('vi-VN')}</p>
                                        <p className="blog-card-excerpt">{relatedPost.content?.substring(0, 100) + '...' || 'Không có tóm tắt'}</p>
                                        <Link to={`/blog/${relatedPost.id}`} className="blog-card-read-more">
                                            Xem thêm
                                        </Link>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default BlogDetailPage;