"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";

interface NavbarProps {
  accessToken: string | null;
}

const Navbar: React.FC<NavbarProps> = ({ accessToken }) => {
  const router = useRouter();
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(!!accessToken);

  useEffect(() => {
    setIsLoggedIn(!!accessToken);
  }, [accessToken]);

  const handleLogout = async () => {
    try {
      const response = await fetch("http://localhost:8080/members/logout", {
        method: "POST",
        credentials: "include",
      });

      if (response.ok) {
        setIsLoggedIn(false);
        router.push("/");
      } else {
        alert("로그아웃 실패");
      }
    } catch (error) {
      console.error("로그아웃 요청 실패:", error);
    }
  };

  return (
      <header className="bg-white shadow-md p-4 flex justify-between items-center">
        {/* ✅ 클릭 시 메인 페이지 이동 기능 추가 */}
        <div
            className="text-2xl font-bold cursor-pointer"
            onClick={() => router.push("/")}
        >
          📚 도서 추천 리뷰 서비스
        </div>

        <div className="flex gap-4">
          {isLoggedIn ? (
              <>
                <button className="px-4 py-2 border rounded-lg" onClick={handleLogout}>로그아웃</button>
                <button className="px-4 py-2 border rounded-lg" onClick={() => router.push("/member/mine")}>내 프로필</button>
                <button className="px-4 py-2 border rounded-lg">🔔</button>
              </>
          ) : (
              <>
                <button className="px-4 py-2 border rounded-lg" onClick={() => router.push("/member/login")}>로그인</button>
                {/* 회원가입 버튼을 로그인 상태에서만 보이게 처리 */}
                <button className="px-4 py-2 border rounded-lg" onClick={() => router.push("/member")}>회원가입</button>
              </>
          )}
        </div>
      </header>
  );
};

export default Navbar;

