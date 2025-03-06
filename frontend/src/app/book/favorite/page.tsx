"use client";

import React, { useState, useEffect } from "react";
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
                <ul>
                    {favoriteBooks.map((book) => (
                        <li key={book.id}>
                            <img src={book.image} alt={book.title} className="book-image" />
                            <div>
                                <h4>{book.title}</h4>
                                <p>{book.author}</p>
                            </div>
                        </li>
                    ))}
                </ul>
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
                    max-width: 800px;
                    margin: 0 auto;
                    padding: 20px;
                    font-family: Arial, sans-serif;
                }

                .favorite-books-container ul {
                    list-style-type: none;
                    padding: 0;
                }

                .favorite-books-container li {
                    display: flex;
                    gap: 20px;
                    margin-bottom: 20px;
                    border-bottom: 1px solid #ccc;
                    padding-bottom: 20px;
                }

                .book-image {
                    width: 100px;
                    height: 150px;
                    object-fit: cover;
                    border-radius: 8px;
                }

                .favorite-books-container h3 {
                    margin-bottom: 20px;
                    font-size: 24px;
                    font-weight: bold;
                    color: #333;
                }

                .favorite-books-container p {
                    font-size: 16px;
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
                    gap: 20px;
                    margin-top: 20px;
                }

                button {
                    padding: 10px 15px;
                    background-color: #007bff;
                    color: white;
                    border: none;
                    cursor: pointer;
                    font-size: 16px;
                    border-radius: 5px;
                    transition: 0.3s;
                }

                button:hover {
                    background-color: #0056b3;
                }

                button:disabled {
                    background-color: #ccc;
                    cursor: not-allowed;
                }
            `}</style>
        </div>
    );
};

export default FavoriteBooks;
