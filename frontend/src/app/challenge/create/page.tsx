"use client";

import { useState } from "react";
import client from "@/lib/client";
import { useRouter } from "next/navigation";

/**
 * 챌린지 생성
 *
 * @author 손진영
 * @since 2025.03.11
 */
export default function CreateChallenge() {
  const [formData, setFormData] = useState({
    name: "",
    content: "",
    startDate: "",
    endDate: "",
  });
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const router = useRouter();

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // 유효성 검사
    if (
      !formData.name ||
      !formData.content ||
      !formData.startDate ||
      !formData.endDate
    ) {
      setErrorMessage("모든 필드를 입력해주세요.");
      return;
    }

    try {
      const response = await client.POST("/challenge/create", {
        body: {
          name: formData.name,
          content: formData.content,
          startDate: formData.startDate,
          endDate: formData.endDate,
        },
      });
      if (response.response.ok) {
        setSuccessMessage(
          response.message || "챌린지가 성공적으로 생성되었습니다."
        );
        router.push("/challenge");
      } else {
        setErrorMessage(response.error.message);
      }
      setFormData({ name: "", content: "", startDate: "", endDate: "" });
      setErrorMessage("");
    } catch (error: any) {
      if (error.response?.data?.errorCode === "CREATE_CHALLENGE") {
        setErrorMessage("챌린지 생성을 위한 권한이 없습니다.");
      } else {
        setErrorMessage("챌린지 생성 중 오류가 발생했습니다.");
      }
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <h1 className="text-3xl font-bold mb-6 text-center">챌린지 생성</h1>
      <form
        onSubmit={handleSubmit}
        className="max-w-lg mx-auto bg-white shadow-md rounded-lg p-6"
      >
        {errorMessage && <p className="text-red-500 mb-4">{errorMessage}</p>}
        {successMessage && (
          <p className="text-green-500 mb-4">{successMessage}</p>
        )}

        <div className="mb-4">
          <label htmlFor="name" className="block text-gray-700 font-bold mb-2">
            챌린지 이름
          </label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="챌린지 이름을 입력하세요"
            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring focus:ring-blue-300"
          />
        </div>

        <div className="mb-4">
          <label
            htmlFor="content"
            className="block text-gray-700 font-bold mb-2"
          >
            챌린지 내용
          </label>
          <textarea
            id="content"
            name="content"
            value={formData.content}
            onChange={handleChange}
            placeholder="챌린지 내용을 입력하세요"
            rows={4}
            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring focus:ring-blue-300"
          />
        </div>

        <div className="mb-4">
          <label
            htmlFor="startDate"
            className="block text-gray-700 font-bold mb-2"
          >
            시작 날짜
          </label>
          <input
            type="date"
            id="startDate"
            name="startDate"
            value={formData.startDate}
            onChange={handleChange}
            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring focus:ring-blue-300"
          />
        </div>

        <div className="mb-4">
          <label
            htmlFor="endDate"
            className="block text-gray-700 font-bold mb-2"
          >
            종료 날짜
          </label>
          <input
            type="date"
            id="endDate"
            name="endDate"
            value={formData.endDate}
            onChange={handleChange}
            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring focus:ring-blue-300"
          />
        </div>

        <button
          type="submit"
          className="w-full bg-blue-500 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-600 transition duration-200"
        >
          생성하기
        </button>
      </form>
    </div>
  );
}
