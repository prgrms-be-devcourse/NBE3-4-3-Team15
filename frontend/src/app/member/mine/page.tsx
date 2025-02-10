"use client";

import client from "@/lib/client";
import React, { useState, useEffect } from "react";
import { UserProfile, ApiResponse } from "@/lib/backend/schema.d"; // UserProfile 타입 import

export default function Mine() {
  const [userProfile, setUserProfile] = useState<UserProfile | null>(null);
  const [isEditing, setIsEditing] = useState(false); // 수정 모드 상태

  // 회원 정보를 가져오는 함수
  const getUserProfile = async () => {
    try {
      const response = await client.GET("/members/mine");
      if (response.data) {
        setUserProfile(response.data.data); // 프로필 데이터 저장
      }
    } catch (error) {
      console.error("회원 정보 조회에 실패하였습니다.", error);
      alert("회원 정보 조회에 실패하였습니다.");
    }
  };

  // 컴포넌트가 마운트될 때 사용자 프로필 가져오기
  useEffect(() => {
    getUserProfile();
  }, []);

  // 컴포넌트가 마운트될 때 사용자 프로필 가져오기
  useEffect(() => {
    getUserProfile();
  }, []);

  // 수정 버튼 클릭 시 호출되는 함수
  const handleEdit = () => {
    setIsEditing(!isEditing);
  };

  // 저장 버튼 클릭 시 호출되는 함수
  const handleSave = async () => {
    try {
      // 여기서 API를 호출하여 수정된 데이터를 저장하는 코드를 추가하세요.
      // 예: await client.PUT('/members/mine', userProfile);

      alert("수정이 완료되었습니다.");
      setIsEditing(false); // 수정 모드 종료
    } catch (error) {
      console.error("수정에 실패하였습니다.", error);
      alert("수정에 실패하였습니다.");
    }
  };

  // 사용자 프로필이 로드되었을 때 화면에 표시
  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <div style={{ margin: "auto" }}>
        {userProfile ? (
          <table>
            <tbody>
              <tr>
                <td>ID</td>
                <td>{userProfile.id}</td>
              </tr>
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
                    <select
                      value={userProfile.gender}
                      onChange={(e) =>
                        setUserProfile({
                          ...userProfile,
                          gender: parseInt(e.target.value),
                        })
                      }
                    >
                      <option value="0">남자</option>
                      <option value="1">여자</option>
                    </select>
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
                      <button
                        className="btn btn-primary mt-2"
                        onClick={handleSave}
                      >
                        저장
                      </button>
                      <button
                        className="btn btn-primary mt-2"
                        onClick={handleEdit}
                      >
                        취소
                      </button>
                    </>
                  ) : (
                    <button
                      className="btn btn-secondary mt-2"
                      onClick={handleEdit}
                    >
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
