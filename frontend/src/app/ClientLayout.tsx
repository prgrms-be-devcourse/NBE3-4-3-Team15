"use client";

import { components } from "@/lib/backend/schema";
import client from "@/lib/client";
import Link from "next/link";
import "@/lib/Navbar.css";
import { useRouter } from "next/navigation";

export default function ClientLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const isLogin = false;

  const logout = async () => {
    const response = await client.DELETE("/members/logout");

    if (response.error) {
      alert(response.error.msg);
      return;
    }

    window.location.replace("/");
  };

  return (
    <div>
      <nav
        className="nav-class"
        style={{ position: "fixed", right: "0", top: "10px" }}
      >
        <ul>
          {!isLogin && (
            <li>
              <Link href="/member">
                <button className="btn btn-sm btn-primary">로그인</button>
              </Link>
            </li>
          )}
          {!isLogin && (
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
          )}
          {isLogin && (
            <li
              style={{
                cursor: "pointer",
                display: "inline-block",
                marginRight: "10px",
              }}
            >
              <Link href="/member/mine">
                <button className="btn btn-sm btn-primary">내 정보</button>
              </Link>
            </li>
          )}
          {isLogin && (
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
          )}
        </ul>
      </nav>
      <main>{children}</main>
    </div>
  );
}
