"use client";

import React, { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import client from "@/lib/client";

interface ReviewDTO {
    id: number;
    bookId: number;
    userId: number;
    content: string;
    rating: number;
    createdAt: string;
    isRecommended: boolean; // 추천 여부 추가
}

const ReviewListPage = () => {
    const { bookId } = useParams();
    const [userProfile, setUserProfile] = useState<{
        id: number | undefined;
        username: string;
        email: string;
        nickname: string;
        gender?: number;
        birth?: string;
    } | null>(null);
    const router = useRouter();
    const [reviews, setReviews] = useState<ReviewDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [reviewContent, setReviewContent] = useState("");
    const [rating, setRating] = useState<number | null>(null);
    const [editingReviewId, setEditingReviewId] = useState<number | null>(null);

    useEffect(() => {
        checkLoginStatus();
        fetchReviews();
    }, [bookId]);

    // ✅ 로그인 상태 확인
    const checkLoginStatus = async () => {
        try {
            const response = await client.GET("/members/mine");
            if (response.data?.data) {
                const { id, username, email, nickname, gender, birth } = response.data.data;
                setUserProfile({ id, username, email, nickname, gender, birth });
            }
        } catch (error) {
            console.error("로그인 상태 확인 실패:", error);
        }
    };

    // ✅ 리뷰 목록 가져오기
    const fetchReviews = async (page = 1, size = 10) => {
        try {
            const response = await fetch(`http://localhost:8080/review/books/${bookId}?page=${page}&size=${size}`);
            if (!response.ok) throw new Error("리뷰를 불러올 수 없습니다.");

            const data = await response.json();
            setReviews(data.data.content);
        } catch (error) {
            console.error("리뷰 조회 실패:", error);
            setError("리뷰를 불러올 수 없습니다.");
        } finally {
            setLoading(false);
        }
    };

    // ✅ 리뷰 작성 (로그인한 사용자만 가능)
    const handleReviewSubmit = async () => {
        if (!userProfile) {
            alert("로그인 후 리뷰를 작성할 수 있습니다.");
            return;
        }

        if (!reviewContent || rating === null) {
            alert("내용과 평점을 입력해주세요.");
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/review`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ bookId, content: reviewContent, rating: rating, userId: userProfile.id }),
                credentials: "include",
            });

            if (!response.ok) throw new Error("리뷰 등록 실패");

            alert("리뷰가 등록되었습니다!");
            setReviewContent("");
            setRating(null);
            fetchReviews(); // ✅ 리뷰 목록 다시 불러오기
        } catch (error) {
            console.error("리뷰 등록 실패:", error);
            alert("리뷰 등록에 실패했습니다.");
        }
    };

    // ✅ 리뷰 수정
    const handleReviewEdit = async (reviewId: number) => {
        if (!userProfile || userProfile.id !== reviews.find((review) => review.id === reviewId)?.userId) {
            alert("다른 사람의 글은 수정할 수 없습니다.");
            return;
        }

        const updatedReview = { content: reviewContent, rating };

        try {
            const response = await fetch(`http://localhost:8080/review/${reviewId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(updatedReview),
                credentials: "include",
            });

            if (!response.ok) throw new Error("리뷰 수정 실패");

            alert("리뷰가 수정되었습니다!");
            setEditingReviewId(null); // 종료 후 편집 모드 해제
            fetchReviews(); // 리뷰 목록 다시 불러오기
        } catch (error) {
            console.error("리뷰 수정 실패:", error);
            alert("리뷰 수정에 실패했습니다.");
        }
    };

    // ✅ 리뷰 삭제
    const handleReviewDelete = async (reviewId: number) => {
        if (!userProfile || userProfile.id !== reviews.find((review) => review.id === reviewId)?.userId) {
            alert("다른 사람의 글은 삭제할 수 없습니다.");
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/review/${reviewId}`, {
                method: "DELETE",
                credentials: "include",
            });

            if (!response.ok) throw new Error("리뷰 삭제 실패");

            alert("리뷰가 삭제되었습니다!");
            fetchReviews(); // 리뷰 목록 다시 불러오기
        } catch (error) {
            console.error("리뷰 삭제 실패:", error);
            alert("리뷰 삭제에 실패했습니다.");
        }
    };

    // ✅ 리뷰 추천
    const handleRecommend = async (reviewId: number) => {
        if (!userProfile) {
            alert("로그인 후 추천할 수 있습니다.");
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/review/${reviewId}/recommend`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ reviewId, memberId: userProfile.id }), // reviewId와 memberId를 body에 포함
                credentials: "include",
            });

            if (!response.ok) throw new Error("리뷰 추천 처리 실패");

            fetchReviews(); // ✅ 리뷰 목록 다시 불러오기
        } catch (error) {
            console.error("리뷰 추천 처리 실패:", error);
            alert("리뷰 추천에 실패했습니다.");
        }
    };

    return (
        <div className="min-h-screen bg-gray-100 p-6 flex justify-center">
            <div className="max-w-4xl w-full bg-white p-8 rounded-lg shadow-lg">
                <button className="text-blue-500 mb-4" onClick={() => router.back()}>
                    ◀ 뒤로가기
                </button>

                <h3 className="text-2xl font-bold">💬 리뷰 목록</h3>
                {loading ? (
                    <p>로딩 중...</p>
                ) : error ? (
                    <p className="text-red-500">{error}</p>
                ) : reviews.length > 0 ? (
                    reviews.map((review) => (
                        <div key={review.id} className="border-b py-4">
                            <p className="text-lg">{review.content}</p>
                            <p className="text-gray-500 text-sm">
                                {Array.from({ length: review.rating }).map((_, i) => (
                                    <span key={i} className="text-yellow-500">⭐</span>
                                ))} ({review.rating}/10)
                            </p>

                            {/* 추천 버튼 */}
                            <div className="flex gap-2 mt-2">
                                <button
                                    className={`text-${review.isRecommended ? 'red' : 'blue'}-500`}
                                    onClick={() => handleRecommend(review.id)}
                                >
                                    {review.isRecommended ? "추천 취소" : "리뷰 추천"}
                                </button>
                            </div>

                            {/* 로그인한 사용자만 수정/삭제 버튼 표시 */}
                            {userProfile?.id === review.userId && (
                                <div className="flex gap-2 mt-2">
                                    <button
                                        className="text-blue-500"
                                        onClick={() => {
                                            setReviewContent(review.content);
                                            setRating(review.rating);
                                            setEditingReviewId(review.id);
                                        }}
                                    >
                                        수정
                                    </button>
                                    <button
                                        className="text-red-500"
                                        onClick={() => handleReviewDelete(review.id)}
                                    >
                                        삭제
                                    </button>
                                </div>
                            )}
                        </div>
                    ))
                ) : (
                    <p className="text-gray-500">아직 리뷰가 없습니다.</p>
                )}

                {/* ✅ 로그인한 사용자만 리뷰 작성 가능 */}
                {userProfile && (
                    <div className="mt-8">
                        <h3 className="text-2xl font-bold">{editingReviewId ? "📖 리뷰 수정" : "📖 리뷰 작성"}</h3>
                        <textarea
                            className="w-full h-24 p-2 border rounded mt-2"
                            placeholder="리뷰를 입력하세요..."
                            value={reviewContent}
                            onChange={(e) => setReviewContent(e.target.value)}
                        />
                        <div className="flex items-center gap-2 mt-2">
                            <label className="text-lg">평점:</label>
                            <input
                                type="number"
                                min="0"
                                max="10"
                                value={rating ?? ""}
                                onChange={(e) => setRating(Number(e.target.value))}
                                className="border p-1 w-16 text-center"
                            />
                        </div>
                        <button
                            className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                            onClick={() =>
                                editingReviewId ? handleReviewEdit(editingReviewId) : handleReviewSubmit()
                            }
                        >
                            {editingReviewId ? "수정하기" : "리뷰 등록"}
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default ReviewListPage;

