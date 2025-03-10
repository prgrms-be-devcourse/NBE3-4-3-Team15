"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import client from "@/lib/client"; // axios나 fetch API 클라이언트
import { components } from "@/lib/backend/schema";

type BookDTO = components["schemas"]["BookDTO"];

const FavoriteBooks = () => {
    const [favoriteBooks, setFavoriteBooks] = useState<BookDTO[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [page, setPage] = useState(1);
    const pageSize = 10;
    const [totalPages, setTotalPages] = useState(1);


    const router = useRouter();

    useEffect(() => {
        fetchFavoriteBooks();
    }, [page]); // page가 변경될 때만 실행

    const fetchFavoriteBooks = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await client.GET(`/book/favorite?page=${page}&size=${pageSize}`);
            const data = response.data;

            if (data?.data) {
                setFavoriteBooks(data.data.content || []);
                setTotalPages(data.data.totalPages || 1);
            } else {
                setFavoriteBooks([]);
            }
        } catch (error) {
            console.error("찜한 도서 목록을 가져오는 데 오류가 발생했습니다:", error);
            setError("도서를 불러오는 중 문제가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="favorite-books-container">
            <h3>📚 찜한 도서 목록</h3>

            {loading ? (
                <p className="loading">⏳ 로딩 중...</p>
            ) : error ? (
                <p className="error">{error}</p>
            ) : favoriteBooks.length > 0 ? (
                <div className="book-grid">
                    {favoriteBooks.map((book) => (
                        <div key={book.id} className="book-card" onClick={() => router.push(`/book/${book.isbn}`)}>
                            <img src={book.image} alt={book.title} className="book-image" />
                            <div className="book-info">
                                <h4>{book.title}</h4>
                                <p>{book.author}</p>
                            </div>
                        </div>
                    ))}
                </div>
            ) : (
                <p>찜한 도서가 없습니다.</p>
            )}

            {/* 페이징 버튼 */}
            <div className="pagination">
                <button disabled={page === 1 || loading} onClick={() => setPage(page - 1)}>⬅ 이전</button>
                <span>{page} / {totalPages}</span>
                <button disabled={page === totalPages || loading} onClick={() => setPage(page + 1)}>다음 ➡</button>
            </div>

            <style jsx>{`
                .favorite-books-container {
                    max-width: 900px;
                    margin: 0 auto;
                    padding: 20px;
                    font-family: 'Arial', sans-serif;
                    text-align: center;
                }

                h3 {
                    font-size: 26px;
                    font-weight: bold;
                    color: #333;
                    margin-bottom: 20px;
                }

                .book-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
                    gap: 20px;
                    justify-content: center;
                    padding: 20px;
                }

                .book-card {
                    background: white;
                    border-radius: 10px;
                    overflow: hidden;
                    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                    cursor: pointer;
                    transition: transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    padding: 10px;
                    text-align: center;
                }

                .book-card:hover {
                    transform: translateY(-5px);
                    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
                }

                .book-image {
                    width: 120px;
                    height: 180px;
                    object-fit: cover;
                    border-radius: 8px;
                }

                .book-info {
                    margin-top: 10px;
                }

                h4 {
                    font-size: 16px;
                    color: #444;
                    font-weight: bold;
                }

                p {
                    font-size: 14px;
                    color: #666;
                }

                .loading {
                    color: #007bff;
                    font-weight: bold;
                }

                .error {
                    color: red;
                    font-weight: bold;
                }

                .pagination {
                    display: flex;
                    justify-content: center;
                    gap: 10px;
                    margin-top: 20px;
                }

                button {
                    padding: 10px 15px;
                    background: linear-gradient(135deg, #007bff, #0056b3);
                    color: white;
                    border: none;
                    cursor: pointer;
                    font-size: 16px;
                    border-radius: 5px;
                    transition: all 0.3s ease;
                }

                button:hover {
                    background: linear-gradient(135deg, #0056b3, #004494);
                }

                button:disabled {
                    background: #ccc;
                    cursor: not-allowed;
                }
            `}</style>
        </div>
    );
};

export default FavoriteBooks;
