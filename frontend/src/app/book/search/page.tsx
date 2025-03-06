"use client";

import React, { useState, useEffect } from "react";
import { useSearchParams, useRouter } from "next/navigation";


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

const SearchPage = () => {
    const searchParams = useSearchParams();
    const query = searchParams.get("query") || "";
    const router = useRouter();

    const [books, setBooks] = useState<BookDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [searchQuery, setSearchQuery] = useState(query);
    const pageSize = 49;

    useEffect(() => {
        if (query) {
            fetchBooks();
        }
    }, [query, page]);

    const fetchBooks = async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8080/book/search?query=${query}&page=${page}&size=${pageSize}`);
            const data = await response.json();
            setBooks(data.data.content);
        } catch (error) {
            console.error("검색 데이터를 가져오는 데 오류가 발생했습니다:", error);
        } finally {
            setLoading(false);
        }
    };

    const nextPage = () => setPage((prev) => prev + 1);
    const prevPage = () => setPage((prev) => (prev > 0 ? prev - 1 : prev));

    const handleSearch = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === "Enter" && searchQuery.trim() !== "") {
            router.push(`/book/search?query=${encodeURIComponent(searchQuery)}`);
            setPage(0);
        }
    };

    return (
        <div className="min-h-screen bg-gray-100 relative">
            {/* ✅ 중복된 <div> 태그 제거 */}
            <div className="min-h-screen bg-gray-100 p-6">
                {/* 검색바 */}
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

                <h2 className="text-2xl font-bold mb-4">"{query}" 검색 결과</h2>

                {loading ? (
                    <p>로딩 중...</p>
                ) : books.length === 0 ? (
                    <p>검색 결과가 없습니다.</p>
                ) : (
                    <>
                        <div className="grid grid-cols-7 gap-3">
                            {books.map((book) => (
                                <div
                                    key={book.isbn}
                                    className="bg-white p-3 rounded-lg shadow-md cursor-pointer"
                                    onClick={() => router.push(`/book/${book.isbn}`)}
                                >
                                    <img src={book.image} alt={book.title} className="w-full h-60 object-cover" />
                                    <h3 className="text-base font-semibold mt-2 line-clamp-2">{book.title}</h3>
                                    <p className="text-sm text-gray-600 line-clamp-1">{book.author}</p>
                                    <p className="text-xs text-gray-500">❤️ {book.favoriteCount ?? 0}</p>
                                </div>

                            ))}
                        </div>

                        {/* 페이지네이션 버튼 */}
                        <div className="flex justify-between mt-6">
                            <button
                                onClick={prevPage}
                                disabled={page === 0}
                                className="px-4 py-2 bg-gray-300 rounded-lg disabled:opacity-50"
                            >
                                ◀ 이전
                            </button>
                            <button onClick={nextPage} className="px-4 py-2 bg-gray-300 rounded-lg">
                                다음 ▶
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default SearchPage;