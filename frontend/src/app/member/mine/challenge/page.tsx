"use client";

import { useEffect, useState } from "react";
import client from "@/lib/client";

/**
 * 내 챌린지 목록
 *
 * @author 손진영
 * @since 2025.02.11
 */
export default function MyChallenges() {
  const [entries, setEntries] = useState([]);
  const [tab, setTab] = useState("ALL");
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0); // 현재 페이지 번호
  const [size, setSize] = useState(10); // 페이지 크기
  const [totalPages, setTotalPages] = useState(1); // 총 페이지 수

  const getEntries = async (status: string) => {
    try {
      const response = await client.GET(
        `/challenge/entry/mine?page=${page}&size=${size}`
      );
      const filteredEntries = response.data?.data.content.filter(
        (entry: any) => {
          if (status === "ALL") return true;
          return entry.challenge.status === status;
        }
      );
      setEntries(filteredEntries || []);
      setTotalPages(response.data?.data.totalPages || 1);
      setLoading(false);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    getEntries(tab);
  }, [tab, page]);

  const handleValidation = async (id: number) => {
    try {
      const response = await client.POST(`/challenge/${id}/validation`);
      alert(response.message || "출석 인증 성공!");
    } catch (error: any) {
      if (error.response?.data?.errorCode) {
        alert(`에러: ${error.response.data.errorCode}`);
      } else {
        alert("인증 중 문제가 발생했습니다.");
      }
    }
  };

  if (loading) return <p>Loading...</p>;

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <h1 className="text-3xl font-bold mb-6 text-center">내 챌린지 목록</h1>
      <div className="flex space-x-4 mb-6 justify-center">
        <button
          onClick={() => {
            setTab("ALL");
            setPage(0); // 탭 변경 시 첫 페이지로 초기화
          }}
          className={`px-4 py-2 rounded ${
            tab === "ALL"
              ? "bg-blue-500 text-white"
              : "bg-gray-200 text-gray-600"
          }`}
        >
          전체
        </button>
        <button
          onClick={() => {
            setTab("WAITING");
            setPage(0); // 탭 변경 시 첫 페이지로 초기화
          }}
          className={`px-4 py-2 rounded ${
            tab === "WAITING"
              ? "bg-blue-500 text-white"
              : "bg-gray-200 text-gray-600"
          }`}
        >
          대기
        </button>
        <button
          onClick={() => {
            setTab("START");
            setPage(0); // 탭 변경 시 첫 페이지로 초기화
          }}
          className={`px-4 py-2 rounded ${
            tab === "START"
              ? "bg-blue-500 text-white"
              : "bg-gray-200 text-gray-600"
          }`}
        >
          시작
        </button>
        <button
          onClick={() => {
            setTab("END");
            setPage(0); // 탭 변경 시 첫 페이지로 초기화
          }}
          className={`px-4 py-2 rounded ${
            tab === "END"
              ? "bg-blue-500 text-white"
              : "bg-gray-200 text-gray-600"
          }`}
        >
          종료
        </button>
        <button
          onClick={() => {
            setTab("REFUNDING");
            setPage(0); // 탭 변경 시 첫 페이지로 초기화
          }}
          className={`px-4 py-2 rounded ${
            tab === "REFUNDING"
              ? "bg-blue-500 text-white"
              : "bg-gray-200 text-gray-600"
          }`}
        >
          환급 중
        </button>
      </div>
      {entries !== undefined && entries.length > 0 ? (
        <div>
          <ul>
            {entries.map((entry: any) => (
              <li
                key={entry.id}
                className={`flex flex-col bg-white shadow-md rounded-lg p-6 mb-4 ${
                  entry.challenge.status === "WAITING"
                    ? "border border-gray-300"
                    : entry.challenge.status === "START"
                    ? "border border-green-500"
                    : entry.challenge.status === "END"
                    ? "border border-red-500"
                    : entry.challenge.status === "REFUNDING"
                    ? "border border-yellow-500"
                    : ""
                }`}
              >
                <div>
                  <h2 className="text-xl font-semibold">
                    {entry.challenge.name}
                  </h2>
                  <p className="text-gray-600">
                    상태:{" "}
                    <span
                      className={`${
                        entry.challenge.status === "WAITING"
                          ? "text-gray-600"
                          : entry.challenge.status === "START"
                          ? "text-green-500"
                          : entry.challenge.status === "END"
                          ? "text-red-500"
                          : entry.challenge.status === "REFUNDING"
                          ? "text-yellow-500"
                          : ""
                      }`}
                    >
                      {entry.challenge.status}
                    </span>
                  </p>
                  <p className="text-gray-600">
                    예치금: {entry.deposit.toLocaleString()}원
                  </p>

                  {/* 진행률 그래프 */}
                  {entry.challenge.status !== "WAITING" && (
                    <div className="mt-4">
                      <p className="text-sm text-gray-600">
                        진행률: {entry.rate}%
                      </p>
                      <div className="w-full bg-gray-200 rounded-full h-[10px]">
                        <div
                          style={{ width: `${entry.rate}%` }}
                          className={`h-[10px] rounded-full ${
                            entry.rate >= 80
                              ? "bg-green-500"
                              : entry.rate >= 50
                              ? "bg-yellow-500"
                              : "bg-red-500"
                          }`}
                        ></div>
                      </div>
                    </div>
                  )}
                </div>

                {/* 출석 인증 버튼 */}
                {entry.challenge.status === "START" && (
                  <div className="mt-4 flex justify-end">
                    <button
                      onClick={() => handleValidation(entry.challenge.id)}
                      className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
                    >
                      출석 인증
                    </button>
                  </div>
                )}
              </li>
            ))}
          </ul>

          {/* 페이징 버튼 */}
          <div className="flex justify-center mt-6 space-x-4">
            <button
              disabled={page === 0}
              onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
              className={`px-4 py-2 rounded ${
                page === 0
                  ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                  : "bg-blue-500 text-white hover:bg-blue-600"
              }`}
            >
              이전
            </button>
            <button
              disabled={page >= totalPages - 1}
              onClick={() =>
                setPage((prev) => Math.min(prev + 1, totalPages - 1))
              }
              className={`px-4 py-2 rounded ${
                page >= totalPages - 1
                  ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                  : "bg-blue-500 text-white hover:bg-blue-600"
              }`}
            >
              다음
            </button>
          </div>
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center h-64 bg-gray-100 rounded-lg">
          <p className="text-gray-500 text-lg">
            {tab === "ALL"
              ? "현재 챌린지가 없습니다."
              : tab === "WAITING"
              ? "현재 대기 중인 챌린지가 없습니다."
              : tab === "START"
              ? "현재 시작된 챌린지가 없습니다."
              : tab === "END"
              ? "현재 종료된 챌린지가 없습니다."
              : tab === "REFUNDING" && "현재 환급 중인 챌린지가 없습니다."}
          </p>
        </div>
      )}
    </div>
  );
}
