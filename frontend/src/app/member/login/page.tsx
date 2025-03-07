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
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);

  // 로그인 요청
  const login = async () => {
    setErrorMessage(""); // 기존 에러 메시지 초기화
    setLoading(true); // 로딩 상태 시작

    try {
      const response = await fetch("http://localhost:8080/members/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: id, password: password }),
        credentials: "include", // 쿠키를 포함하도록 설정
      });

      if (response.ok) {
        localStorage.setItem("username", id); // 저장
        window.location.href = "/";
      } else {
        const errorData = await response.json();
        setErrorMessage(errorData.message || "알 수 없는 오류");
      }
    } catch (error) {
      console.error("로그인에 실패하였습니다:", error);
      setErrorMessage("로그인에 실패하였습니다.");
    } finally {
      setLoading(false); // 로딩 상태 종료
    }
  };

  // 소셜 로그인
  const handleSocialLogin = (provider: string) => {
    const oauthUrl = `http://localhost:8080/oauth2/authorization/${provider}`;
    window.location.href = oauthUrl;
  };

  return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 p-8">
        <div className="w-full max-w-md bg-white shadow-lg rounded-2xl p-8">
          <h1 className="text-2xl font-semibold text-gray-800 mb-6 text-center">
            로그인
          </h1>

          {errorMessage && (
              <div className="text-red-500 mb-4 text-center">{errorMessage}</div>
          )}

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                ID
              </label>
              <input
                  type="text"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
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
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
              />
            </div>
          </div>

          <div className="mt-6 flex justify-between">
            <button
                className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg"
                onClick={() => (window.location.href = "/member")}
            >
              회원가입
            </button>
            <button
                onClick={login}
                className={`${
                    loading ? "bg-gray-400 cursor-not-allowed" : "bg-blue-500"
                } text-white px-4 py-2 rounded-lg hover:bg-blue-600 focus:outline-none`}
                disabled={loading}
            >
              {loading ? "로딩 중..." : "로그인"}
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

