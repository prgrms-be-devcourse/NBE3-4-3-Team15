"use client";

import React, { useState } from "react";
import Link from "next/link";
import { cursorTo } from "readline";
import "./Navbar.css";
import { FiX } from "react-icons/fi";

const Navbar: React.FC = () => {
  return (
    <>
      <nav
        className="nav-class"
        style={{ position: "fixed", right: "0", top: "10px" }}
      >
        <ul>
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
        </ul>
      </nav>
    </>
  );
};

export default Navbar;
