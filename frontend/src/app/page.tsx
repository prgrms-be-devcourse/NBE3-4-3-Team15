"use client";

import { Margarine } from "next/font/google";
import Image from "next/image";
import { CiSearch } from "react-icons/ci";
import "@/lib/searchBook.css";
import { useState } from "react";
import { useRouter } from "next/navigation";

export default function Home() {
  const router = useRouter();
  const [query, setQuery] = useState("");

  const handleChange = (e) => {
    setQuery(e.target.value);
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      // enter 했을 때의 코드 작성
      // if(e.keyCode === 13) 도 사용가능하다.
      router.push(`/book?query=${query}`);
    }
  };

  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <div className="border rounded search-bar">
        <CiSearch
          style={{
            display: "inline-block",
            float: "left",
            width: "20px",
            height: "20px",
            color: "black",
          }}
        />
        <input
          placeholder="검색하고자 하는 책"
          style={{ display: "inline-block", float: "left", marginLeft: "3px" }}
          value={query}
          onChange={handleChange}
          onKeyDown={handleKeyDown}
        ></input>
      </div>
    </div>
  );
}
