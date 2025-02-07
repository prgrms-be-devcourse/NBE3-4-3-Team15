"use client";

import { Margarine } from "next/font/google";
import Image from "next/image";
import client from "@/lib/client";
import React, { useMemo, useState, useEffect } from "react";
import { useRouter } from "next/navigation";

export default function Login() {
  const router = useRouter();
  const [id, setId] = useState("");
  const [password, setPassword] = useState("");

  const login = async () => {
    try {
      const response = await client.POST("/members/login", {
        body: {
          username: id,
          password: password,
        },
      });
      router.replace(`/`);
    } catch (error) {
      console.error("로그인에 실패하였습니다.");
      alert("로그인에 실패하였습니다.");
    }
  };

  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <div>
        <table>
          <tbody>
            <tr>
              <td>ID</td>
              <td>
                <input
                  type="text"
                  className="border rounded"
                  value={id}
                  onChange={(e) => setId(e.target.value)}
                  required
                />
              </td>
            </tr>
            <tr>
              <td>Password</td>
              <td>
                <input
                  type="password"
                  className="border rounded"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </td>
            </tr>
            <tr>
              <td>
                <button className="btn btn-primary mt-2">회원가입</button>
              </td>
              <td style={{ textAlign: "end" }}>
                <button className="btn btn-primary mt-2" onClick={login}>
                  로그인
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}
