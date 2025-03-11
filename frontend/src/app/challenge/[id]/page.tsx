"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import client from "@/lib/client";

/**
 * 챌린지 상세
 *
 * @author 손진영
 * @since 2025.03.11
 */
export default function ChallengeDetail() {
  const { id } = useParams();
  const [challenge, setChallenge] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [deposit, setDeposit] = useState(0);
  const [isParticipated, setIsParticipated] = useState(false);

  useEffect(() => {
    if (id) {
      client
        .GET(`/challenge/${id}`)
        .then((response) => setChallenge(response.data?.data))
        .catch((err) => console.error(err))
        .finally(() => setLoading(false));

      client
        .GET(`/challenge/entry/${id}`)
        .then((response) => {
          if (response.response.ok) {
            setIsParticipated(true);
          }
        })
        .catch((err) => console.error(err))
        .finally(() => setLoading(false));
    }
  }, [id]);

  const join = async () => {
    try {
      const response = await client.POST(`/challenge/${id}/join`, {
        body: {
          deposit: deposit,
        },
      });
      if (response.response.ok) {
        alert(response.message || "참여가 완료되었습니다.");
        setIsParticipated(true);
        window.location.reload();
      } else {
        alert(response.error.message);
      }
    } catch (error: any) {
      if (error.response?.data?.errorCode) {
        alert(`에러: ${error.response.data.errorCode}`);
      } else {
        alert("참여 중 문제가 발생했습니다.");
      }
    }
  };

  const quit = async () => {
    try {
      const response = await client.DELETE(`/challenge/${id}/join`);
      console.log(response);
      if (response.response.ok) {
        alert(response.message || "참여가 취소되었습니다.");
        setIsParticipated(false);
        window.location.reload();
      } else {
        alert(response.error.message);
      }
    } catch (error: any) {
      if (error.response?.data?.errorCode) {
        alert(`에러: ${error.response.data.errorCode}`);
      } else {
        alert("참여 중 문제가 발생했습니다.");
      }
    }
  };

  if (loading || !challenge)
    return <p className="text-center mt-10">Loading...</p>;

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <h1 className="text-3xl font-bold mb-6 text-center">챌린지 상세 정보</h1>
      <div className="bg-white shadow-md rounded-lg p-6">
        <h2 className="text-xl font-semibold">{challenge.name}</h2>
        <p className="mt-2 text-gray-600">{challenge.content}</p>
        <p className="mt-2 text-gray-600">상태: {challenge.status}</p>
        <p className="mt-2 text-gray-600">
          총 예치금: {challenge.totalDeposit.toLocaleString()}원
        </p>

        <div className="mt-6 flex flex-col space-y-4">
          <input
            type="number"
            value={deposit}
            onChange={(e) => setDeposit(Number(e.target.value))}
            placeholder="예치금을 입력하세요"
            className="px-4 py-2 border border-gray-300 rounded"
          />
          {isParticipated ? (
            <button
              onClick={quit}
              className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
            >
              참여취소
            </button>
          ) : (
            <button
              onClick={quit}
              className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
              onClick={join}
              className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
            >
              참여하기
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
