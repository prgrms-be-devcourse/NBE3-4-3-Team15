// Navbar.tsx
"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import "./Navbar.css";

interface NavbarProps {
  accessToken: string | null;
}

const Navbar: React.FC<NavbarProps> = ({ accessToken }) => {
  // accessToken 값에 따라 초기 로그인 상태 설정
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(!!accessToken);
  console.log(isLoggedIn);

  useEffect(() => {
    // accessToken 값이 바뀔 때 로그인 상태 업데이트
    setIsLoggedIn(!!accessToken);
  }, [accessToken]);

  const handleLogout = async () => {
    // 로그아웃 처리 로직 추가 (쿠키 삭제 등)
    try {
      const response = await fetch("http://localhost:8080/members/logout", {
        method: "POST",
        credentials: "include",
      });

      if (response.ok) {
        setIsLoggedIn(false); // 로그아웃 상태로 변경
      } else {
        alert("로그아웃 실패");
      }
    } catch (error) {
      console.error("로그아웃 요청에 실패했습니다.", error);
    }
  };

  return (
    <nav
      className="nav-class"
      style={{ position: "fixed", right: "0", top: "10px" }}
    >
      <ul>
        {isLoggedIn ? (
          <>
            <li>
              <Link href="/member/mine">
                <button className="btn btn-sm btn-primary">내정보</button>
              </Link>
            </li>
            <li
              style={{
                cursor: "pointer",
                display: "inline-block",
                marginRight: "10px",
              }}
            >
              <button className="btn btn-sm btn-primary" onClick={handleLogout}>
                로그아웃
              </button>
            </li>
          </>
        ) : (
          <>
            <li>
              <Link href="/member">
                <button className="btn btn-sm btn-primary">로그인</button>
              </Link>
            </li>
            <li
              style={{
                cursor: "pointer",
                display: "inline-block",
                marginRight: "10px",
              }}
            >
              <Link href="/member/join">
                <button className="btn btn-sm btn-primary">회원가입</button>
              </Link>
            </li>
          </>
        )}
      </ul>
    </nav>
  );
};

export default Navbar;
