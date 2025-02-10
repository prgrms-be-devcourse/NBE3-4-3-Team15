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
  const [book, setBook] = useState({});
  const param = useSearchParams();

  console.log(param.get("book"));

  return (
    <div>
      <div>
        <span>{book.title}</span>
        <span>{book.image}</span>
        <span>{book.author}</span>
        <span>{book.isbn}</span>
      </div>
      <div></div>
    </div>
  );
}
