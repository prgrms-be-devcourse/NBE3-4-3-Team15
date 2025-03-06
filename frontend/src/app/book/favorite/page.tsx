"use client";

import React, { useState, useEffect } from "react";
import client from "@/lib/client";  // axios나 fetch API 클라이언트
import { components } from "@/lib/backend/schema";

type BookDTO = components["schemas"]["BookDTO"];

const FavoriteBooks = () => {
    const [favoriteBooks, setFavoriteBooks] = useState<BookDTO[]>([]);  // 초기값 빈 배열
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchFavoriteBooks();
    },);

    const fetchFavoriteBooks = async () => {
        setLoading(true);
        try {
            const response = await client.GET('/book/favorite', {
            });
            const data = response.data;
            setFavoriteBooks(data?.data || []);  // data가 없으면 빈 배열로 처리
        } catch (error) {
            console.error("찜한 도서 목록을 가져오는 데 오류가 발생했습니다:", error);
        } finally {
            setLoading(false);
        }
    };


    return (
        <div className="favorite-books-container">
            <h3>찜한 도서 목록</h3>
            {loading ? (
                <p>로딩 중...</p>
            ) : (
                <>
                    {favoriteBooks.length > 0 ? (
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
                </>
            )}

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

                .pagination {
                    display: flex;
                    justify-content: center;
                    gap: 20px;
                    margin-top: 20px;
                }

                button {
                    padding: 10px;
                    background-color: #007bff;
                    color: white;
                    border: none;
                    cursor: pointer;
                    font-size: 16px;
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
