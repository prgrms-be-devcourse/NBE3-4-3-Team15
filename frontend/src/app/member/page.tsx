"use client";

import React, { useState } from "react";

/**
 * 로그인 페이지
 *
 * @author 손진영
 * @since 2025.02.11
 */
const Login: React.FC = () => {
  const [id, setId] = useState("");
  const [password, setPassword] = useState("");

  // 로그인 요청
  const login = async () => {
    try {
      const response = await fetch("http://localhost:8080/members/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: id, password: password }),
        credentials: "include", // 쿠키를 포함하도록 설정
      });

      if (response.ok) {
        window.location.href = "/";
      } else {
        const errorData = await response.json();
        alert(
          `로그인에 실패하였습니다: ${errorData.message || "알 수 없는 오류"}`
        );
      }
    } catch (error) {
      console.error("로그인에 실패하였습니다:", error);
      alert("로그인에 실패하였습니다.");
    }
  };

  // 소셜 로그인
  const handleSocialLogin = (provider) => {
    // Spring Boot OAuth2 로그인 엔드포인트로 리다이렉션
    const oauthUrl = `http://localhost:8080/oauth2/authorization/${provider}`;
    window.location.href = oauthUrl;
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-8">
      <div className="w-full max-w-md bg-white shadow-lg rounded-2xl p-8">
        <h1 className="text-2xl font-semibold text-gray-800 mb-6 text-center">
          로그인
        </h1>

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              ID
            </label>
            <input
              type="text"
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
              value={id}
              onChange={(e) => setId(e.target.value)}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Password
            </label>
            <input
              type="password"
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
        </div>

        <div className="mt-6 flex justify-between">
          <button
            className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg"
            onClick={() => (window.location.href = "/member/join")}
          >
            회원가입
          </button>
          <button
            onClick={login}
            className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600"
          >
            로그인
          </button>
        </div>

        <div className="mt-8 text-center">
          <p className="text-sm text-gray-500 mb-3">소셜 로그인</p>
          <div className="flex flex-col space-y-3 items-center">
            <button
              className="w-full bg-red-500 text-white px-6 py-2 text-center rounded-lg hover:bg-red-600 transition-all"
              onClick={() => handleSocialLogin("google")}
            >
              구글 로그인
            </button>
            <button
              className="w-full bg-green-500 text-white px-6 py-2 text-center rounded-lg hover:bg-green-600 transition-all"
              onClick={() => handleSocialLogin("naver")}
            >
              네이버 로그인
            </button>
            <button
              className="w-full bg-yellow-400 text-white px-6 py-2 text-center rounded-lg hover:bg-yellow-500 transition-all"
              onClick={() => handleSocialLogin("kakao")}
            >
              카카오 로그인
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
