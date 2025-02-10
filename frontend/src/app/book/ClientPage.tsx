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

  const query = param.get("query");

  const [books, setBooks] = useState([
    {
      id: "1",
      title: "test1",
      image: "test1",
      author: "test1",
      isbn: "1",
    },
    {
      id: "2",
      title: "test2",
      image: "test2",
      author: "test2",
      isbn: "2",
    },
    {
      id: "3",
      title: "test3",
      image: "test3",
      author: "test3",
      isbn: "3",
    },
    {
      id: "4",
      title: "test4",
      image: "test4",
      author: "test4",
      isbn: "4",
    },
    {
      id: "5",
      title: "test5",
      image: "test5",
      author: "test5",
      isbn: "5",
    },
  ]);

  useEffect(() => {
    const search = async () => {
      try {
        const resposne = await client.GET("/book", {
          params: {
            query: {
              query,
            },
          },
        });
      } catch (error) {
        console.error(error);
      }
    };

    search();
  });

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
          style={{ display: "inline-block", float: "left", marginLeft: "3px" }}
        ></input>
      </div>
      <div style={{ position: "fixed", top: "50px", left: "20px" }}>
        {books.map((book) => (
          <Link href={{ pathname: "/book/" + book.id }} key={book.id}>
            <div>
              <span>{book.title}</span>
              <span>{book.image}</span>
              <span>{book.author}</span>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
