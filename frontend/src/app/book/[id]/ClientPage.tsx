"use client";

import { Margarine } from "next/font/google";
import Image from "next/image";
import { CiSearch } from "react-icons/ci";
import "@/lib/searchBook.css";
import client from "@/lib/client";
import { useEffect, useState } from "react";
import { useRouter, useParams, usePathname } from "next/navigation";
import Link from "next/link";

export default function ClientPage() {
  const router = useRouter();
  const [book, setBook] = useState({});
  const pathname = usePathname();

  const search = async () => {
    const response = await client.GET(pathname);

    const data = response.data.data;

    setBook(data);
  };

  useEffect(() => {
    search();
  }, []);

  return (
    <div>
      <div style={{ display: "flex", justifyContent: "center" }}>
        <span>{book.title}</span>
        <img src={book.image} alt="" />
        <span>{book.author}</span>
        <span>{book.description}</span>
        <span>{book.isbn}</span>
      </div>
      <div></div>
    </div>
  );
}
