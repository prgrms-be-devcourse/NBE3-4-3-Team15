"use client";

import React, { useState, useEffect } from "react";
import Link from "next/link";
import "./Navbar.css";

interface NavbarProps {
  accessToken: string | null;
  setAccessToken: (token: string | null) => void;
}

const Navbar: React.FC<NavbarProps> = ({ accessToken, setAccessToken }) => {
  useEffect(() => {
    // 상태가 변경되면 상위 컴포넌트에도 변경 사항 전달
    if (accessToken) setAccessToken(accessToken);
  }, [accessToken, setAccessToken]);

  /**
   * 로그아웃
   */
  const logout = () => {
    localStorage.removeItem("accessToken");
    setAccessToken(null);
  };

  return (
    <nav
      className="nav-class"
      style={{ position: "fixed", right: "0", top: "10px" }}
    >
      <ul>
        {accessToken ? (
          <>
            <li
              style={{
                cursor: "pointer",
                display: "inline-block",
                marginRight: "10px",
              }}
            >
              <button className="btn btn-sm btn-primary" onClick={logout}>
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
