"use client";

import client from "@/lib/client";
import React, { useState } from "react";
import { useRouter } from "next/navigation";

export default function join() {
  const router = useRouter();
  const [id, setId] = useState("");
  const [password1, setPassword1] = useState("");
  const [password2, setPassword2] = useState("");
  const [email, setEmail] = useState("");
  const [nickname, setNickname] = useState("");
  const [gender, setGender] = useState(0);
  const [birth, setBirth] = useState("");

  const join = async () => {
    try {
      const response = await client.POST("/members", {
        body: {
          username: id,
          password1: password1,
          password2: password2,
          email: email,
          nickname: nickname,
          gender: gender,
          birth: birth,
        },
      });
      router.replace(`/`);
    } catch (error) {
      console.error("회워가입에 실패하였습니다.");
      alert("회원가입에 실패하였습니다.");
    }
  };

  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <div style={{ margin: "auto" }}>
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
                  value={password1}
                  onChange={(e) => setPassword1(e.target.value)}
                  required
                />
              </td>
            </tr>
            <tr>
              <td>Password 확인</td>
              <td>
                <input
                  type="password"
                  className="border rounded"
                  value={password2}
                  onChange={(e) => setPassword2(e.target.value)}
                  required
                />
              </td>
            </tr>
            <tr>
              <td>Email</td>
              <td>
                <input
                  type="text"
                  className="border rounded"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </td>
            </tr>
            <tr>
              <td>별명</td>
              <td>
                <input
                  type="text"
                  className="border rounded"
                  value={nickname}
                  onChange={(e) => setNickname(e.target.value)}
                  required
                />
              </td>
            </tr>
            <tr>
              <td>성별</td>
              <td>
                <label>
                  <input
                    type="radio"
                    value="0"
                    checked={gender === "0"}
                    onChange={(e) => setGender(e.target.value)} // 남자 선택 시 상태 업데이트
                    required
                  />
                  남자
                </label>
                <label>
                  <input
                    type="radio"
                    value="1"
                    checked={gender === "1"}
                    onChange={(e) => setGender(e.target.value)} // 여자 선택 시 상태 업데이트
                    required
                  />
                  여자
                </label>
              </td>
            </tr>
            <tr>
              <td>생년월일</td>
              <td>
                <input
                  type="date"
                  className="border rounded"
                  value={birth}
                  onChange={(e) => setBirth(e.target.value)}
                  style={{ width: "198px" }}
                />
              </td>
            </tr>
            <tr>
              <td></td>
              <td style={{ textAlign: "end" }}>
                <button className="btn btn-primary mt-2" onClick={join}>
                  회원가입
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}
