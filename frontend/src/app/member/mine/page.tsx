"use client";

import client from "@/lib/client";
import React, { useState, useEffect } from "react";
import type { components } from "@/lib/backend/schema";

type MineDto = components["schemas"]["MineDto"];

/**
 * 회원 프로필 조회, 수정
 *
 * @author 손진영
 * @since 2025.02.09
 */
export default function Mine() {
  const [userProfile, setUserProfile] = useState<MineDto | null>(null);
  const [isEditing, setIsEditing] = useState(false);

  // 회원 정보 조회
  const getUserProfile = async () => {
    try {
      const response = await client.GET("/members/mine");
      if (response.data) {
        const { email, nickname, gender, birth } = response.data.data;
        setUserProfile({ email, nickname, gender, birth });
      }
    } catch (error) {
      console.error("회원 정보 조회에 실패하였습니다.", error);
      alert("회원 정보 조회에 실패하였습니다.");
    }
  };

  useEffect(() => {
    getUserProfile();
  }, []);

  // 수정 상태 저장
  const edit = () => {
    setIsEditing(!isEditing);
  };

  // 수정 요청
  const modify = async () => {
    try {
      await client.PUT("/members/mine", {
        body: userProfile,
      });

      alert("수정이 완료되었습니다.");
      setIsEditing(false);
    } catch (error) {
      console.error("수정에 실패하였습니다.", error);
      alert("수정에 실패하였습니다.");
    }
  };

  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <div style={{ margin: "auto" }}>
        {userProfile ? (
          <table>
            <tbody>
              <tr>
                <td>Email</td>
                <td>
                  {isEditing ? (
                    <input
                      type="text"
                      value={userProfile.email}
                      onChange={(e) =>
                        setUserProfile({
                          ...userProfile,
                          email: e.target.value,
                        })
                      }
                    />
                  ) : (
                    userProfile.email
                  )}
                </td>
              </tr>
              <tr>
                <td>별명</td>
                <td>
                  {isEditing ? (
                    <input
                      type="text"
                      value={userProfile.nickname}
                      onChange={(e) =>
                        setUserProfile({
                          ...userProfile,
                          nickname: e.target.value,
                        })
                      }
                    />
                  ) : (
                    userProfile.nickname
                  )}
                </td>
              </tr>
              <tr>
                <td>성별</td>
                <td>
                  {isEditing ? (
                    <>
                      <label>
                        <input
                          type="radio"
                          value="0"
                          checked={userProfile.gender === 0}
                          onChange={(e) =>
                            setUserProfile({
                              ...userProfile,
                              gender: parseInt(e.target.value),
                            })
                          }
                          required
                        />
                        남자
                      </label>
                      <label>
                        <input
                          type="radio"
                          value="1"
                          checked={userProfile.gender === 1}
                          onChange={(e) =>
                            setUserProfile({
                              ...userProfile,
                              gender: parseInt(e.target.value),
                            })
                          }
                          required
                        />
                        여자
                      </label>
                    </>
                  ) : userProfile.gender === 0 ? (
                    "남자"
                  ) : (
                    "여자"
                  )}
                </td>
              </tr>
              <tr>
                <td>생년월일</td>
                <td>
                  {isEditing ? (
                    <input
                      type="date"
                      value={userProfile.birth}
                      onChange={(e) =>
                        setUserProfile({
                          ...userProfile,
                          birth: e.target.value,
                        })
                      }
                    />
                  ) : (
                    userProfile.birth
                  )}
                </td>
              </tr>
              <tr>
                <td></td>
                <td style={{ textAlign: "end" }}>
                  {isEditing ? (
                    <>
                      <button className="btn btn-primary mt-2" onClick={modify}>
                        저장
                      </button>
                      <button className="btn btn-primary mt-2" onClick={edit}>
                        취소
                      </button>
                    </>
                  ) : (
                    <button className="btn btn-secondary mt-2" onClick={edit}>
                      수정
                    </button>
                  )}
                </td>
              </tr>
            </tbody>
          </table>
        ) : (
          <p>로딩 중...</p>
        )}
      </div>
    </div>
  );
}
