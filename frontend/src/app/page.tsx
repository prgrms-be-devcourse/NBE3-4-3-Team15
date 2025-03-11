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
  const [weeklyBooks, setWeeklyBooks] = useState([]);
  const [weeklyReviews, setWeeklyReviews] = useState([]);
  const [dailyReviews, setDailyReviews] = useState([]);

  const router = useRouter();

  // Best Top 100
  useEffect(() => {
    fetchBestSellers();
  }, [page]);

  // 랭킹 시스템
  useEffect(() => {
    getWeeklyBookRanking();
    getWeeklyReviewRanking();
    getDailyReviewRanking();
  }, []);

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

  const getWeeklyBookRanking = async () => {
    try {
      const response = await client.GET("/ranking/weekly/book");
      const data = response.data?.data;
      setWeeklyBooks((WeeklyBooks) => data);
    } catch (error) {
      console.error("주간 도서 랭킹 가져오기 오류:", error);
    }
  };

  const getWeeklyReviewRanking = async () => {
    try {
      const response = await client.GET("/ranking/weekly/review");
      const data = response.data?.data;
      setWeeklyReviews((weeklyReviews) => data);
    } catch (error) {
      console.error("주간 리뷰 랭킹 가져오기 오류:", error);
    }
  };

  const getDailyReviewRanking = async () => {
    try {
      const response = await client.GET("/ranking/daily/review");
      const data = response.data?.data;
      setDailyReviews((dailyReviews) => data);
    } catch (error) {
      console.error("일일 리뷰 랭킹 가져오기 오류:", error);
    }
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

      <section className="px-6 py-4 flex gap-4">
        {/* 왼쪽: 주간 인기 도서 & 실시간 베스트셀러 */}
        <div className="flex-1 space-y-6">
          {/* 주간 인기 도서 TOP 10 */}
          <div className="w-full bg-white p-4 rounded-lg shadow-md">
            <h2 className="text-xl font-semibold mb-3 text-black">주간 인기 도서 TOP 10</h2>
            <div className="grid grid-cols-5 gap-5">
              {weeklyBooks.map((book) => (
                <div
                    key={book.rank}
                    className="bg-gray-200 p-4 rounded-lg shadow w-full flex flex-col items-center"
                >
                  <p className="font-bold text-md text-black mt-2">{book.rank}</p>
                  <img
                      src={book.image}
                      alt={book.title}
                      className="w-24 h-32 object-cover rounded-md"
                  />
                  <p className="text-sm text-black text-center w-full truncate">
                    {book.title}
                  </p>
                </div>
              ))}
            </div>
          </div>

        {/* 실시간 베스트셀러 TOP 100 */}
        <div className="w-full bg-white p-4 rounded-lg shadow-md">
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
      </div>

        {/* 오른쪽: 일간 급상승 리뷰 & 주간 추천 리뷰 */}
        <div className="w-[20vw] space-y-6">
          {/* 일간 급상승 리뷰 */}
          <div className="bg-white p-4 rounded-lg shadow">
            <h2 className="text-xl font-semibold mb-3 text-black">일간 급상승 리뷰 TOP 5</h2>
            <div className="flex flex-col gap-4">
              {dailyReviews.map((review) => (
                  <div
                      key={review.rank}
                      className="bg-gray-200 p-4 rounded-lg shadow w-full h-[100px] flex flex-col items-center justify-center overflow-hidden"
                  >
                    <p className="font-semibold text-lg text-black text-center truncate w-full">
                      {review.rank}. {review.title}
                    </p>
                    <p className="font-semibold text-sm text-black text-center truncate w-full">
                      {review.content}
                    </p>
                  </div>
              ))}
            </div>
          </div>

          {/* 주간 추천 리뷰 */}
          <div className="bg-white p-4 rounded-lg shadow">
            <h2 className="text-xl font-semibold mb-3 text-black">주간 추천 리뷰 TOP 10</h2>
            <div className="grid grid-cols-1 gap-4">
              {weeklyReviews.map((review) => (
                  <div
                      key={review.rank}
                      className="bg-gray-200 p-4 rounded-lg shadow w-full h-[100px] flex flex-col items-center justify-center overflow-hidden"
                  >
                    <p className="font-semibold text-lg text-black text-center truncate w-full">
                      {review.rank}. {review.title}
                    </p>
                    <p className="font-semibold text-sm text-black text-center truncate w-full">
                      {review.content}
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
