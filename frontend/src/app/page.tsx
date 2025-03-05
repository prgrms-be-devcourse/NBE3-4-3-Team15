"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

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

    const router = useRouter();

    useEffect(() => {
        fetchBestSellers();
    }, [page]);

    const fetchBestSellers = async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8080/book?page=${page}&size=10`);
            const data = await response.json();
            setBestSellers(data.data.content);
        } catch (error) {
            console.error("베스트셀러 데이터를 가져오는 데 오류가 발생했습니다:", error);
        } finally {
            setLoading(false);
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

            {/* 실시간 베스트셀러 TOP 100 */}
            <section className="px-6 py-4 flex gap-4">
                <div className="absolute left-[1%] w-[75vw] bg-white p-4 rounded-lg shadow-md">
                    <h2 className="text-xl font-semibold mb-3">
                        실시간 베스트셀러 TOP 100 <span className="text-sm text-gray-500">(3월 5일 12:00 기준)</span>
                    </h2>

                    <div className="flex justify-between mb-2">
                        <button onClick={prevPage} className="px-4 py-2 bg-gray-300 rounded-lg">◀ 이전</button>
                        <button onClick={nextPage} className="px-4 py-2 bg-gray-300 rounded-lg">다음 ▶</button>
                    </div>

                    {loading ? (
                        <div>로딩 중...</div>
                    ) : (
                        <div className="grid grid-cols-5 gap-5">
                            {bestSellers.map((book) => (
                                <div key={book.isbn} className="bg-gray-300 rounded-md overflow-hidden relative">
                                    <div className="w-full h-[80%]">
                                        <img src={book.image} alt={book.title} className="w-full h-full object-cover" />
                                    </div>
                                    <div className="p-2">
                                        <p className="text-sm text-center font-semibold">{book.title}</p>
                                        <p className="text-xs text-center text-gray-600">{book.author}</p>
                                    </div>
                                    <div className="absolute top-2 left-2 bg-red-500 text-white text-xs px-2 py-1 rounded-md">
                                        {book.ranking}
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>


                {/* 오른쪽 영역: 일간 급상승 리뷰와 주간 추천 리뷰 */
                }
                <div className="absolute top-[160px] right-[1%] w-[20vw] space-y-6">
                    {/* 일간 급상승 리뷰 */}
                    <div className="bg-white p-4 rounded-lg shadow">
                        <h2 className="text-xl font-semibold mb-3">일간 급상승 리뷰 TOP 5</h2>
                        <div className="flex flex-col gap-6">
                            {Array.from({length: 5}).map((_, index) => (
                                <div
                                    key={index}
                                    className="bg-gray-200 p-6 rounded-lg shadow w-full h-[100px] flex items-center justify-center"
                                >
                                    <p className="font-semibold text-lg text-center">급상승 리뷰 {index + 1}</p>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* 주간 추천 리뷰 */}
                    <div className="bg-white p-4 rounded-lg shadow">
                        <h2 className="text-xl font-semibold mb-3">주간 추천 리뷰 TOP 10</h2>
                        <div className="grid grid-cols-1 gap-6">
                            {Array.from({length: 10}).map((_, index) => (
                                <div
                                    key={index}
                                    className="bg-gray-200 p-6 rounded-lg shadow w-full h-[100px] flex items-center justify-center"
                                >
                                    <p className="font-semibold text-lg text-center">리뷰 {index + 1}</p>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </section>
        </div>
    )
        ;
};

export default Page;