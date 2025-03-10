"use client";

import React, { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import {AiFillHeart, AiOutlineHeart} from "react-icons/ai";

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
    const [liked, setLiked] = useState(false); // 찜 상태 저장

    // ✅ 책 정보 가져오기 (로그인 없이 접근 가능)
    const fetchBookDetail = async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8080/book/${isbn}`);
            if (!response.ok) throw new Error("도서 정보를 가져올 수 없습니다.");

            const data = await response.json();
            setBook(data.data);
            setLiked(data.data.isFavorited); // 서버에서 찜 여부 받아오기
        } catch (error) {
            console.error("도서 정보 조회 실패:", error);
            setError("도서 정보를 불러올 수 없습니다.");
        } finally {
            setLoading(false);
        }
    };

    const toggleLike = async () => {
        if (!book) return;

        try {
            const response = await fetch(`http://localhost:8080/book/${isbn}/favorite`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(book), // bookDTO 전송
                credentials: "include", // 로그인 정보 포함 (쿠키 기반 인증 사용 시)
            });

            if (!response.ok) {
                throw new Error("찜하기 요청 실패");
            }

            const data = await response.json();
            alert(data.message); // "찜한 도서가 추가되었습니다." 또는 "찜한 도서가 취소되었습니다."

            // 찜 상태 반전 후, 새로운 favoriteCount 값으로 업데이트
            setLiked((prev) => !prev);

            // book이 null이 아님을 보장하고, favoriteCount만 업데이트
            setBook((prevBook) => {
                if (prevBook) {
                    return {
                        ...prevBook,
                        favoriteCount: data.favoriteCount, // 서버에서 받은 최신 favoriteCount로 업데이트
                    };
                }
                return prevBook; // prevBook이 null일 경우 그대로 반환
            });
        } catch (error) {
            console.error("찜하기 요청 실패:", error);
            alert("찜하기 요청에 실패했습니다.");
        }
    };

    useEffect(() => {
        fetchBookDetail();
    }, [isbn]);

    return (
        <div className="min-h-screen bg-gray-100 p-6 flex justify-center">
            {loading ? (
                <p>로딩 중...</p>
            ) : error ? (
                <p className="text-red-500">{error}</p>
            ) : book ? (
                <div className="max-w-4xl w-full bg-white p-8 rounded-lg shadow-lg">
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
                            <p className="text-sm text-gray-400 mt-1">📌 ISBN: {book.isbn}</p>
                        </div>
                        {/* 좋아요 & ISBN */}
                        <div className="mt-3 text-center">
                            <button
                                className={`flex items-center gap-1 text-lg ${liked ? "text-red-500" : "text-gray-500"}`}
                                onClick={toggleLike} // ✅ 좋아요 클릭 시 API 요청
                            >
                                {liked ? <AiFillHeart /> : <AiOutlineHeart />}
                                <span>{liked ? "찜 취소" : "찜하기"} ({book.favoriteCount ?? 0})</span>
                            </button>
                            <p className="text-sm text-gray-400 mt-1">📌 ISBN: {book.isbn}</p>
                        </div>

                        {/* 책 정보 */}
                        <div className="md:w-2/3 md:ml-6 mt-6 md:mt-0">
                            {book.ranking && (
                                <p className="text-lg font-semibold text-red-500">🏆 {book.ranking}위</p>
                            )}
                            <h2 className="text-3xl font-bold">{book.title}</h2>
                            <p className="text-lg text-gray-600 mt-2">
                                저자: <span className="font-semibold">{book.author}</span>
                            </p>
                            <p className="text-sm text-gray-500 mt-2 leading-relaxed">{book.description}</p>
                        </div>
                    </div>

                    {/* ✅ 리뷰 목록 조회 버튼 */}
                    <div className="mt-8">
                        <button
                            className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
                            onClick={() => router.push(`/review/books/${book?.id}`)}
                        >
                            💬 모든 리뷰 보기
                        </button>
                    </div>
                </div>
            ) : (
                <p>도서를 찾을 수 없습니다.</p>
            )}
        </div>
    );
};

export default BookDetailPage;
