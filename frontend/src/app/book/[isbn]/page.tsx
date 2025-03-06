"use client";

import React, { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { AiOutlineHeart, AiFillHeart, AiOutlineShareAlt } from "react-icons/ai";

interface BookDTO {
    id: number;
    title: string;
    author: string;
    description: string;
    image: string;
    isbn: string;
    ranking: number | null;
    favoriteCount: number | null;
}

const BookDetailPage = () => {
    const { isbn } = useParams();
    const router = useRouter();
    const [book, setBook] = useState<BookDTO | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [liked, setLiked] = useState(false);

    useEffect(() => {
        if (!isbn) return;
        fetchBookDetail();
    }, [isbn]);

    const fetchBookDetail = async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8080/book/${isbn}`);
            if (!response.ok) {
                throw new Error("도서 정보를 가져올 수 없습니다.");
            }
            const data = await response.json();
            setBook(data.data);
        } catch (error) {
            console.error("도서 정보 조회 실패:", error);
            setError("도서 정보를 불러올 수 없습니다.");
        } finally {
            setLoading(false);
        }
    };

    const toggleLike = () => {
        setLiked(!liked);
    };

    const shareBook = () => {
        navigator.clipboard.writeText(window.location.href);
        alert("링크가 복사되었습니다!");
    };

    return (
        <div className="min-h-screen bg-gray-100 p-6 flex justify-center">
            {loading ? (
                <p>로딩 중...</p>
            ) : error ? (
                <p className="text-red-500">{error}</p>
            ) : book ? (
                <div className="max-w-4xl w-full bg-white p-8 rounded-lg shadow-lg">
                    {/* 뒤로가기 버튼 */}
                    <button className="text-blue-500 mb-4" onClick={() => router.back()}>
                        ◀ 뒤로가기
                    </button>

                    <div className="flex flex-col md:flex-row">
                        {/* 책 이미지 */}
                        <div className="md:w-1/3 flex flex-col items-center">
                            <img
                                src={book.image}
                                alt={book.title}
                                className="w-60 h-80 object-cover rounded-lg shadow-lg"
                            />

                            {/* 좋아요 & ISBN */}
                            <div className="mt-3 text-center">
                                <button
                                    className={`flex items-center gap-1 text-lg ${liked ? "text-red-500" : "text-gray-500"}`}
                                    onClick={toggleLike}
                                >
                                    {liked ? <AiFillHeart /> : <AiOutlineHeart />}
                                    <span>{liked ? "좋아요 취소" : "좋아요"} ({book.favoriteCount ?? 0})</span>
                                </button>
                                <p className="text-sm text-gray-400 mt-1">📌 ISBN: {book.isbn}</p>
                            </div>
                        </div>

                        {/* 책 정보 */}
                        <div className="md:w-2/3 md:ml-6 mt-6 md:mt-0">
                            {/* 📌 랭킹을 책 제목 위에 배치 */}
                            {book.ranking && (
                                <p className="text-lg font-semibold text-red-500">🏆 {book.ranking}위</p>
                            )}
                            <h2 className="text-3xl font-bold">{book.title}</h2>
                            <p className="text-lg text-gray-600 mt-2">저자: <span className="font-semibold">{book.author}</span></p>
                            <p className="text-sm text-gray-500 mt-2 leading-relaxed">{book.description}</p>

                            {/* 공유 버튼 */}
                            <div className="flex items-center gap-4 mt-4">
                                <button
                                    className="flex items-center gap-1 text-lg text-gray-500 hover:text-blue-500"
                                    onClick={shareBook}
                                >
                                    <AiOutlineShareAlt />
                                    <span>공유</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            ) : (
                <p>도서를 찾을 수 없습니다.</p>
            )}
        </div>
    );
};

export default BookDetailPage;



