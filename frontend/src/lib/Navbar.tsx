"use client";

import React, { useState, useEffect } from "react";
import Link from "next/link";
import "./Navbar.css";

interface NavbarProps {
  accessToken: string | null;
  setAccessToken: (token: string | null) => void;
}

const Navbar: React.FC = () => {
  return (
    <nav
      className="nav-class"
      style={{ position: "fixed", right: "0", top: "10px" }}
    >
      <ul>
        {false ? (
          <>
            <li
              style={{
                cursor: "pointer",
                display: "inline-block",
                marginRight: "10px",
              }}
            >
              <button className="btn btn-sm btn-primary">로그아웃</button>
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
