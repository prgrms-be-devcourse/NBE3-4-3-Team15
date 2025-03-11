"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import client from "@/lib/client";

export default function Home() {
  const [challenges, setChallenges] = useState([]);
  const [myChallenges, setMyChallenges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loading2, setLoading2] = useState(true);
  const [tab, setTab] = useState("WAITING");

  const getChallenges = async (status: string) => {
    const response = await client.GET(`/challenge?status=${status}`);
    setChallenges(response.data?.data);
    setLoading(false);
  };

  const getMyChallenges = async () => {
    const response = await client.GET(`/challenge/entry/mine`);
    setMyChallenges(response.data?.data);
    setLoading2(false);
  };

  useEffect(() => {
    getChallenges(tab);
    getMyChallenges();
  }, [tab]);

  if (loading && loading2) return <p>Loading...</p>;

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <h1 className="text-3xl font-bold mb-6 text-center">챌린지 목록</h1>
      <div className="flex space-x-4 mb-6">
        <button
          onClick={() => setTab("WAITING")}
          className={`px-4 py-2 rounded ${
            tab === "WAITING"
              ? "bg-blue-500 text-white"
              : "bg-gray-200 text-gray-600"
          }`}
        >
          대기
        </button>
        <button
          onClick={() => setTab("START")}
          className={`px-4 py-2 rounded ${
            tab === "START"
              ? "bg-blue-500 text-white"
              : "bg-gray-200 text-gray-600"
          }`}
        >
          시작
        </button>
        <button
          onClick={() => setTab("END")}
          className={`px-4 py-2 rounded ${
            tab === "END"
              ? "bg-blue-500 text-white"
              : "bg-gray-200 text-gray-600"
          }`}
        >
          종료
        </button>
      </div>
      {challenges != undefined && challenges.length > 0 ? (
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
              {myChallenges !== undefined &&
              myChallenges.some(
                (myChallenge) => myChallenge.challengeId === challenge.id
              ) ? (
                <p className="text-green-600">참가 중</p>
              ) : (
                <p className="text-gray-600">참가 가능</p>
              )}
            </Link>
          ))}
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
