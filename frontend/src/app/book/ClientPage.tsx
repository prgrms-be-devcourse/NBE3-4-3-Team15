"use client";

import { Margarine } from "next/font/google";
import Image from "next/image";
import { CiSearch } from "react-icons/ci";
import "@/lib/searchBook.css";
import client from "@/lib/client";
import { useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import Link from "next/link";

export default function ClientPage() {
  const router = useRouter();

  const param = useSearchParams();

  const [query, setQuery] = useState(param.get("query"));

  const [searchQuery, setSearchQuery] = useState(query);

  const handleChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      // enter 했을 때의 코드 작성
      // if(e.keyCode === 13) 도 사용가능하다.
      setQuery(searchQuery);
      router.push(`/book?query=${query}&page=1`);
    }
  };

  const page = param.get("page");

  const [books, setBooks] = useState([]);

  const [pages, setPages] = useState([]);

  const [curPage, setCurPage] = useState(1);

  const [lastPage, setLastPage] = useState(0);

  const search = async () => {
    try {
      const response = await client.GET("/book", {
        params: {
          query: {
            query,
            page,
          },
        },
      });

      const data = response.data?.data;

      setBooks(data.content);

      let pagelist = [];

      for (let i = 0; i < data.totalPages; i++) {
        pagelist.push(i + 1);
      }

      setPages(pagelist);
      setLastPage(data.totalPages);
      setCurPage(page);
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    search();
  }, [query, page]);

  return (
    <div>
      <div
        className="border rounded search-bar"
        style={{ position: "fixed", top: "15px", left: "20px", width: "80vw" }}
      >
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
          style={{
            display: "inline-block",
            float: "left",
            marginLeft: "3px",
            width: "calc(100% - 23px)",
          }}
          value={searchQuery}
          onChange={handleChange}
          onKeyDown={handleKeyDown}
        ></input>
      </div>
      <div
        style={{
          position: "fixed",
          top: "50px",
          left: "20px",
          overflow: "auto",
          height: "90vh",
        }}
      >
        {books.map((book) => (
          <Link
            href={{
              pathname: "/book/" + book.isbn,
              query: {
                book: JSON.stringify(book),
              },
            }}
            as={"/book/" + book.isbn}
            key={book.isbn}
          >
            <div>
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "50px 50px 850px 50px 100px",
                }}
              >
                <img src={book.image} alt="" style={{ height: "50px" }} />
                <span>제목: </span>
                <span>{book.title}</span>
                <span>작가: </span>
                <span>{book.author}</span>
              </div>
            </div>
          </Link>
        ))}
      </div>
      <div style={{ position: "fixed", bottom: "0", left: "45%" }}>
        <ul className="pagination">
          <Link href={`?query=${query}&page=1`}>
            <li>
              <button
                className="btn btn-sm btn-primary"
                disabled={curPage == 1}
              >
                Previous
              </button>
            </li>
          </Link>
          {pages.map((page) => (
            <Link key={page} href={`?query=${query}&page=${page}`}>
              <li>
                <button className="btn btn-sm">{page}</button>
              </li>
            </Link>
          ))}
          <Link href={`?query=${query}&page=${lastPage}`}>
            <li>
              <button
                className="btn btn-sm btn-primary"
                disabled={lastPage == curPage}
              >
                Last
              </button>
            </li>
          </Link>
        </ul>
      </div>
    </div>
  );
}
