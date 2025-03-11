"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import client from "@/lib/client";

export default function Home() {
  const [challenges, setChallenges] = useState([]);
  const [myChallenges, setMyChallenges] = useState<any[]>([]); // 초기값을 빈 배열로 설정
  const [loading, setLoading] = useState(true);
  const [loading2, setLoading2] = useState(true);
  const [tab, setTab] = useState("WAITING");
  const [page, setPage] = useState(0); // 현재 페이지 번호
  const [size, setSize] = useState(10); // 페이지 크기
  const [totalPages, setTotalPages] = useState(1); // 총 페이지 수

  console.log("내 챌린지 목록:", myChallenges);

  // 챌린지 목록 가져오기
  const getChallenges = async (status: string) => {
    const response = await client.GET(
      `/challenge?status=${status}&page=${page}&size=${size}`
    );
    setChallenges(response.data?.data.content || []); // 현재 페이지 데이터
    setTotalPages(response.data?.data.totalPages || 1); // 총 페이지 수
    setLoading(false);
  };

  // 내 챌린지 목록 가져오기
  const getMyChallenges = async () => {
    const response = await client.GET(`/challenge/entry/mine`);
    setMyChallenges(response.data?.data.content || []); // 예상 데이터 구조에 따라 수정
    setLoading2(false);
  };

  useEffect(() => {
    getChallenges(tab);
    getMyChallenges();
  }, [tab, page]);

  if (loading && loading2) return <p>Loading...</p>;

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <h1 className="text-3xl font-bold mb-6 text-center">챌린지 목록</h1>
      <div className="flex space-x-4 mb-6">
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
      </div>
      {challenges.length > 0 ? (
        <div>
          {/* 챌린지 목록 */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {challenges.map((challenge: any) => (
              <Link
                className="block bg-white shadow-md rounded-lg p-6 hover:bg-gray-50"
                key={challenge.id}
                href={`/challenge/${challenge.id}`}
              >
                <h2 className="text-xl font-semibold">{challenge.name}</h2>
                <p className="text-gray-600">상태: {challenge.status}</p>
                <p className="text-gray-600">
                  총 예치금: {challenge.totalDeposit.toLocaleString()}원
                </p>
                {/* 상태에 따른 메시지 표시 */}
                {tab === "WAITING" ? (
                  myChallenges !== undefined &&
                  myChallenges.some(
                    (myChallenge) => myChallenge.challenge.id === challenge.id
                  ) ? (
                    <p className="text-green-600">참가 중</p>
                  ) : (
                    <p className="text-gray-600">참가 가능</p>
                  )
                ) : tab === "START" ? (
                  myChallenges !== undefined &&
                  myChallenges.some(
                    (myChallenge) => myChallenge.challenge.id === challenge.id
                  ) ? (
                    <p className="text-green-600">참가 중</p>
                  ) : (
                    <p className="text-red-500">챌린지 참가 불가</p>
                  )
                ) : tab === "END" ? (
                  <p className="text-gray-500">챌린지 종료</p>
                ) : null}
              </Link>
            ))}
          </div>

          {/* 페이징 버튼 */}
          <div className="flex justify-center mt-6 space-x-4">
            <button
              disabled={page === 0}
              onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
              className={`px-4 py-2 rounded ${
                page === 0
                  ? "bg-gray-300 text-gray-500"
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
                  ? "bg-gray-300 text-gray-500"
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
            {tab === "WAITING"
              ? "현재 대기 중인 챌린지가 없습니다."
              : tab === "START"
              ? "현재 시작된 챌린지가 없습니다."
              : "현재 종료된 챌린지가 없습니다."}
          </p>
        </div>
      )}
    </div>
  );
}
