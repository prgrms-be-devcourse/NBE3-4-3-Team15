"use client";

import client from "@/lib/client";
import React, { useState } from "react";
import { useRouter } from "next/navigation";

/**
 * 회원가입 페이지
 *
 * @author 손진영
 * @since 2025.02.11
 */
export default function Join() {
  const router = useRouter();
  const [id, setId] = useState("");
  const [password1, setPassword1] = useState("");
  const [password2, setPassword2] = useState("");
  const [email, setEmail] = useState("");
  const [nickname, setNickname] = useState("");
  const [gender, setGender] = useState(0); // 0: 남자, 1: 여자
  const [birth, setBirth] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  // 회원가입 요청
  const join = async () => {
    if (password1 !== password2) {
      setErrorMessage("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
      return;
    }
    try {
      const response = await client.POST("/members", {
        body: {
          username: id,
          password1: password1,
          password2: password2,
          email: email,
          nickname: nickname,
          gender: gender.toString(), // gender를 숫자가 아닌 문자열로 전달
          birth: birth,
        },
      });

      if (response.response.ok) {
        // 회원가입 성공 후 자동 로그인 처리
        const loginResponse = await client.POST("/members/login", {
          body: {
            username: id,
            password: password1,
          },
        });

        if (loginResponse.response.ok) {
          // 로그인 성공 후 홈으로 이동
          router.replace("/");
        } else {
          setErrorMessage("자동 로그인에 실패하였습니다.");
        }
      } else {
        const errorDetails = response.error.errorDetails;
        let errorMessage = "";
        for (let i = 0; i < errorDetails.length; i++) {
          errorMessage +=
              errorDetails[i].field === "password1"
                  ? "Password"
                  : errorDetails[i].field === "password2"
                      ? "Password 확인"
                      : errorDetails[i].field;
          errorMessage += "는 " + errorDetails[i].reason + "\n";
        }
        setErrorMessage(errorMessage);
      }
    } catch (error) {
      console.error("회원가입에 실패하였습니다.");
      setErrorMessage("회원가입에 실패하였습니다.");
    }
  };

  return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100 py-8 px-4 sm:px-6 lg:px-8">
        <div className="w-full max-w-md bg-white p-8 rounded-lg shadow-lg">
          <h2 className="text-2xl font-semibold text-center mb-6">회원가입</h2>

          {errorMessage && <div className="text-red-500 mb-4">{errorMessage}</div>}

          <form className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">ID</label>
              <input
                  type="text"
                  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  value={id}
                  onChange={(e) => setId(e.target.value)}
                  required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Password</label>
              <input
                  type="password"
                  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  value={password1}
                  onChange={(e) => setPassword1(e.target.value)}
                  required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Password 확인</label>
              <input
                  type="password"
                  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  value={password2}
                  onChange={(e) => setPassword2(e.target.value)}
                  required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Email</label>
              <input
                  type="email"
                  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">별명</label>
              <input
                  type="text"
                  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  value={nickname}
                  onChange={(e) => setNickname(e.target.value)}
                  required
              />
            </div>

            <div>
              <span className="block text-sm font-medium text-gray-700">성별</span>
              <div className="flex items-center gap-4">
                <label className="flex items-center">
                  <input
                      type="radio"
                      value={0}
                      checked={gender === 0}
                      onChange={() => setGender(0)}
                      required
                      className="form-radio text-blue-500"
                  />
                  <span className="ml-2">남자</span>
                </label>
                <label className="flex items-center">
                  <input
                      type="radio"
                      value={1}
                      checked={gender === 1}
                      onChange={() => setGender(1)}
                      required
                      className="form-radio text-pink-500"
                  />
                  <span className="ml-2">여자</span>
                </label>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">생년월일</label>
              <input
                  type="date"
                  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  value={birth}
                  onChange={(e) => setBirth(e.target.value)}
                  required
              />
            </div>

            <div className="text-right">
              <button
                  type="button"
                  onClick={join}
                  className="w-full bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-400"
              >
                회원가입
              </button>
            </div>
          </form>
        </div>
      </div>
  );
}
