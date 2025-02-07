import { Margarine } from "next/font/google";
import Image from "next/image";
import { CiSearch } from "react-icons/ci";
import "@/lib/searchBook.css";

export default function Home() {
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
        ></input>
      </div>
    </div>
  );
}
