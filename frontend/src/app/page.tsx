"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import client from "@/lib/client";

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

const Page = () => {
  const [page, setPage] = useState(0);
  const [bestSellers, setBestSellers] = useState<BookDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [updateTime, setUpdateTime] = useState(""); // 🕒 최신 기준 시간 상태 추가
  const [weeklyReviews, setWeeklyReviews] = useState([]);
  const [dailyReviews, setDailyReviews] = useState([]);

  const router = useRouter();

  // Best Top 100
  useEffect(() => {
    fetchBestSellers();
  }, [page]);

  // 랭킹 시스템
  useEffect(() => {
    getWeeklyReviewRanking();
    getDailyReviewRanking();
  });

  // 현재 시간
  useEffect(() => {
    updateCurrentTime();
    const interval = setInterval(updateCurrentTime, 60 * 1000); // 매 분마다 업데이트
    return () => clearInterval(interval);
  }, []);

  // 🕒 현재 시간 기준을 계산하는 함수
  const updateCurrentTime = () => {
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();

    // 매시간 `10분`이 되면 그 시간 기준으로 설정
    const displayHour = minutes >= 10 ? hours : hours - 1;

    setUpdateTime(
      `${now.getMonth() + 1}월 ${now.getDate()}일 ${displayHour}시 기준`
    );
  };

  const fetchBestSellers = async () => {
    setLoading((loading) => true);
    try {
      const response = await fetch(
        `http://localhost:8080/book?page=${page}&size=10`
      );
      const data = await response.json();
      setBestSellers(data.data.content);
    } catch (error) {
      console.error(
        "베스트셀러 데이터를 가져오는 데 오류가 발생했습니다:",
        error
      );
    } finally {
      setLoading((loading) => false);
    }
  };

  const getWeeklyReviewRanking = async () => {
    try {
      const response = client.GET("/ranking/weekly/review");
      const data = response.data?.data;
      setWeeklyReviews((weeklyReviews) => data);
    } catch (error) {}
  };

  const getDailyReviewRanking = async () => {
    try {
      const response = client.GET("/ranking/daily/review");
      const data = response.data?.data;
      setDailyReviews((dailyReviews) => data);
    } catch (error) {}
  };

  const nextPage = () => setPage((prev) => (prev < 9 ? prev + 1 : prev));
  const prevPage = () => setPage((prev) => (prev > 0 ? prev - 1 : prev));

  const handleSearch = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && searchQuery.trim() !== "") {
      router.push(`/book/search?query=${encodeURIComponent(searchQuery)}`);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 relative">
      <div className="flex justify-center py-6">
        <input
          type="text"
          placeholder="검색어를 입력하세요..."
          className="w-1/2 p-3 border rounded-lg shadow-sm"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          onKeyDown={handleSearch}
        />
      </div>

      {/* 실시간 베스트셀러 TOP 100 */}
      <section className="px-6 py-4 flex gap-4">
        <div className="absolute left-[1%] w-[75vw] bg-white p-4 rounded-lg shadow-md">
          <h2 className="text-xl font-semibold mb-3">
            실시간 베스트셀러 TOP 100{" "}
            <span className="text-sm text-gray-500">({updateTime})</span>
          </h2>

          <div className="flex justify-between mb-2">
            <button
              onClick={prevPage}
              className="px-4 py-2 bg-gray-300 rounded-lg"
            >
              ◀ 이전
            </button>
            <button
              onClick={nextPage}
              className="px-4 py-2 bg-gray-300 rounded-lg"
            >
              다음 ▶
            </button>
          </div>

          {loading ? (
            <div>로딩 중...</div>
          ) : (
            <div className="grid grid-cols-5 gap-5">
              {bestSellers.map((book) => (
                <div
                  key={book.isbn}
                  className="bg-gray-300 rounded-md overflow-hidden relative cursor-pointer"
                  onClick={() => router.push(`/book/${book.isbn}`)} // ✅ 클릭 시 상세 페이지 이동
                >
                  <div className="w-full h-[80%]">
                    <img
                      src={book.image}
                      alt={book.title}
                      className="w-full h-full object-cover"
                    />
                  </div>
                  <div className="p-2">
                    <p className="text-sm text-center font-semibold">
                      {book.title}
                    </p>
                    <p className="text-xs text-center text-gray-600">
                      {book.author}
                    </p>
                  </div>
                  <div className="absolute top-2 left-2 bg-red-500 text-white text-xs px-2 py-1 rounded-md">
                    {book.ranking}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* 오른쪽 영역: 일간 급상승 리뷰와 주간 추천 리뷰 */}
        <div className="absolute top-[160px] right-[1%] w-[20vw] space-y-6">
          {/* 일간 급상승 리뷰 */}
          <div className="bg-white p-4 rounded-lg shadow">
            <h2 className="text-xl font-semibold mb-3">
              일간 급상승 리뷰 TOP 5
            </h2>
            <div className="flex flex-col gap-6">
              {dailyReviews.map((review) => (
                <div
                  key={review.rank}
                  className="bg-gray-200 p-6 rounded-lg shadow w-full h-[100px] flex items-center justify-center"
                >
                  <p className="font-semibold text-lg text-center">
                    {review.rank}. {rank.title}
                  </p>
                  <p className="font-semibold text-sm text-center">
                    {review.content}
                  </p>
                  <p className="font-semibold text-sm text-center">
                    {review.score}
                  </p>
                </div>
              ))}
            </div>
          </div>

          {/* 주간 추천 리뷰 */}
          <div className="bg-white p-4 rounded-lg shadow">
            <h2 className="text-xl font-semibold mb-3">
              주간 추천 리뷰 TOP 10
            </h2>
            <div className="grid grid-cols-1 gap-6">
              {weeklyReviews.map((review) => (
                <div
                  key={review.rank}
                  className="bg-gray-200 p-6 rounded-lg shadow w-full h-[100px] flex items-center justify-center"
                >
                  <p className="font-semibold text-lg text-center">
                    {review.rank}. {rank.title}
                  </p>
                  <p className="font-semibold text-sm text-center">
                    {review.content}
                  </p>
                  <p className="font-semibold text-sm text-center">
                    {review.score}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Page;
