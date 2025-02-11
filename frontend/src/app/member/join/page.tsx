"use client";

import { Margarine } from "next/font/google";
import Image from "next/image";
import client from "@/lib/client";
import React, { useMemo, useState, useEffect } from "react";
import { useRouter } from "next/navigation";

export default function Login() {
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

      if (response.response.ok) {
        router.replace("/");
      } else if (!response.response.ok) {
        const errorDetails = response.error.errorDetails;
        let errorMeesage = "";
        for (let i = 0; i < errorDetails.length; i++) {
          errorMeesage +=
            errorDetails[i].field == "password1"
              ? "Password"
              : errorDetails[i].field == "password2"
              ? "Password 확인"
              : errorDetails[i].field;
          errorMeesage += "는 " + errorDetails[i].reason + "\n";
        }
        alert(errorMeesage);
        router.replace("/");
      }
    } catch (error) {
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
              <td
                style={{
                  display: "grid",
                  gridTemplateColumns: "repeat(2, 90px)",
                }}
              >
                <label style={{ margin: "auto" }}>
                  <input
                    type="radio"
                    className="border rounded"
                    value={0}
                    checked={gender == 0}
                    onChange={() => setGender(0)}
                  />
                  <span style={{ marginLeft: "10px" }}>남자</span>
                </label>
                <label style={{ margin: "auto" }}>
                  <input
                    type="radio"
                    className="border rounded"
                    value={1}
                    checked={gender == 1}
                    onChange={() => setGender(1)}
                  />
                  <span style={{ marginLeft: "10px" }}>여자</span>
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
