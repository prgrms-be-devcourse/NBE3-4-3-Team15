"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import client from "@/lib/client";

export default function Home() {
  const [challenges, setChallenges] = useState([]);
  const [loading, setLoading] = useState(true);

  console.log(challenges);

  const getChallenges = async () => {
    const respoonse = await client.GET("/challenge?status=WAITING");
    setChallenges(respoonse.data.data);
    setLoading(false);
  };

  useEffect(() => {
    getChallenges();
  }, []);

  if (loading) return <p>Loading...</p>;

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <h1 className="text-3xl font-bold mb-6 text-center">챌린지 목록</h1>
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
            </Link>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center h-64 bg-gray-100 rounded-lg">
          <p className="text-gray-500 text-lg">
            현재 대기 중인 챌린지가 없습니다.
          </p>
        </div>
      )}
    </div>
  );
}
