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

  const clickButton = (e) => {
    movePage(e.target.value);
  };

  const movePage = (pageNum) => {
    router.push(`/book?query=${query}&page=${pageNum}`);
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      // enter 했을 때의 코드 작성
      // if(e.keyCode === 13) 도 사용가능하다.
      setQuery(searchQuery);
      movePage(1);
    }
  };

  const page = param.get("page");

  const [books, setBooks] = useState([]);

  const [pages, setPages] = useState([]);

  const [curPage, setCurPage] = useState(1);

  const [lastPage, setLastPage] = useState(0);

  const [curPageGroup, setCurPageGroup] = useState(1);

  const [start, setStart] = useState(1);

  const [end, setEnd] = useState(10);

  useEffect(() => {
    const search = async () => {
      try {
        const response = await client.GET("/book", {
          params: {
            query: {
              query,
              page: page - 1,
            },
          },
        });

        const data = response.data?.data;

        setBooks(data.content);

        let pagelist = [];

        setStart(
          page % 10 == 0
            ? 1 + Math.floor((page - 1) / 10) * 10
            : 1 + Math.floor(page / 10) * 10
        );

        setEnd(
          data.totalPages / 10 < Math.floor((page - 1) / 10) + 1
            ? data.totalPages
            : (Math.floor((page - 1) / 10) + 1) * 10
        );

        for (let i = start; i <= end; i++) {
          pagelist.push(i);
        }

        setPages(pagelist);
        setLastPage(data.totalPages);
        setCurPage(page);
      } catch (error) {
        console.error(error);
      }
    };
    search();
  }, [query, page, start, end]);

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
          width: "100%",
        }}
      >
        {books.map((book) => (
          <Link
            href={{
              pathname: "/book/" + book.id,
              query: {
                book: JSON.stringify(book),
              },
            }}
            as={"/book/" + book.id}
            key={book.isbn}
          >
            <div>
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "50px 50px 850px 50px 100px",
                  gridGap: "25px",
                }}
              >
                <img src={book.image} alt="" style={{ height: "85px" }} />
                <span>제목: </span>
                <span>{book.title}</span>
                <span>작가: </span>
                <span>{book.author}</span>
              </div>
            </div>
          </Link>
        ))}
      </div>
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "flex-end",
          height: "100vh",
        }}
      >
        <ul className="pagination">
          <li>
            <button
              className="btn btn-sm btn-primary"
              disabled={start == 1}
              onClick={clickButton}
              value={start - 1}
            >
              Previous
            </button>
          </li>

          {pages.map((page) => (
            <Link key={page} href={`?query=${query}&page=${page}`}>
              <li>
                <button className="btn btn-sm ml-3">{page}</button>
              </li>
            </Link>
          ))}

          <li>
            <button
              className="btn btn-sm btn-primary ml-3"
              disabled={lastPage == end || start / 10 == end / 10}
              onClick={clickButton}
              value={end + 1}
            >
              Next
            </button>
          </li>
        </ul>
      </div>
    </div>
  );
}
