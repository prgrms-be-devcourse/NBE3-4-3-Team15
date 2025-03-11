"use client";

import client from "@/lib/client";
import React, { useState, useEffect } from "react";
import Modal from "react-modal";
import type { components } from "@/lib/backend/schema";
import { useRouter } from "next/navigation";

type MineDto = components["schemas"]["MineDto"];

/**
 * 회원 프로필 조회, 수정, 탈퇴 페이지
 *
 * @author 손진영
 * @since 2025.02.11
 */
export default function Mine() {
  const [userProfile, setUserProfile] = useState<MineDto | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false); // 비밀번호 변경 모달 상태
  const [currentPassword, setCurrentPassword] = useState(""); // 비밀번호 상태
  const [newPassword, setNewPassword] = useState(""); // 새 비밀번호 상태
  const router = useRouter();

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

  //비밀번호 변경 모달 상태 변경
  const passwordEdit = () => {
    setIsPasswordModalOpen(!isPasswordModalOpen);
  };

  // 비밀번호 변경 요청
  const changePassword = async () => {
    try {
      await client.PUT("/members/mine/password", {
        body: {
          currentPassword: currentPassword,
          newPassword: newPassword,
        },
      });

      alert("비밀번호가 변경되었습니다.");
      setIsPasswordModalOpen(false);
      setCurrentPassword("");
      setNewPassword("");
    } catch (error) {
      console.error("비밀번호 변경에 실패하였습니다.", error);
      alert("비밀번호 변경에 실패하였습니다.");
    }
  };

  //회원 탈퇴 요청
  const quit = async () => {
    try {
      if (!confirm("정말 탈퇴하시겠습니까?")) return;
      const response = await client.DELETE("/members/mine", {
        body: {
          password: currentPassword,
        },
      });
      if (response.response.ok) {
        alert("탈퇴 완료되었습니다.");
        setCurrentPassword("");
        window.location.href = "/";
      } else {
        alert("비밀번호가 맞지 않습니다.");
      }
    } catch (error) {
      console.error("탈퇴에 실패하였습니다.", error);
      alert("탈퇴에 실패하였습니다.");
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
                      <div style={{ display: "flex", gap: "5px" }}>
                        <button
                          className="btn btn-primary mt-2"
                          onClick={modify}
                        >
                          저장
                        </button>
                        <button className="btn btn-primary mt-2" onClick={edit}>
                          취소
                        </button>
                      </div>
                    </>
                  ) : (
                    <>
                      <div style={{ display: "flex", gap: "5px" }}>
                        <button
                          className="btn btn-secondary mt-2"
                          onClick={edit}
                        >
                          정보 수정
                        </button>
                        <button
                          className="btn btn-secondary mt-2"
                          onClick={passwordEdit}
                        >
                          비밀번호 변경
                        </button>
                        <button
                          className="btn btn-secondary mt-2"
                          onClick={() => router.push("/member/mine/challenge")}
                        >
                          챌린지
                        </button>
                      </div>
                    </>
                  )}
                </td>
              </tr>
            </tbody>
          </table>
        ) : (
          <p>로딩 중...</p>
        )}
        <button
          className="btn btn-danger mt-5"
          style={{
            fontSize: "1.5rem",
            padding: "15px 30px",
            backgroundColor: "#ff4d4d",
            color: "#fff",
            border: "2px solid #ff1a1a",
            borderRadius: "8px",
            textTransform: "uppercase",
            transition: "all 0.3s ease",
            boxShadow: "0 4px 15px rgba(255, 0, 0, 0.5)",
            cursor: "pointer",
          }}
          onMouseOver={(e) => {
            e.currentTarget.style.transform = "scale(1.05)";
            e.currentTarget.style.boxShadow = "0 6px 20px rgba(255, 0, 0, 0.7)";
          }}
          onMouseOut={(e) => {
            e.currentTarget.style.transform = "scale(1)";
            e.currentTarget.style.boxShadow = "0 4px 15px rgba(255, 0, 0, 0.5)";
          }}
          onClick={() => setIsDeleteModalOpen(true)}
        >
          회원 탈퇴
        </button>
        <Modal
          isOpen={isPasswordModalOpen}
          onRequestClose={() => setIsPasswordModalOpen(false)}
          style={{
            content: {
              top: "50%",
              left: "50%",
              right: "auto",
              bottom: "auto",
              marginRight: "-50%",
              transform: "translate(-50%, -50%)",
              padding: "30px",
              border: "none",
              borderRadius: "15px",
              backgroundColor: "#f9f9f9",
              color: "#333",
              boxShadow: "0 8px 30px rgba(0, 0, 0, 0.2)",
            },
          }}
          ariaHideApp={false}
        >
          <h2
            style={{
              textAlign: "center",
              marginBottom: "20px",
              fontSize: "1.5rem",
              fontWeight: "600",
            }}
          >
            🔒 비밀번호 변경
          </h2>
          <input
            type="password"
            placeholder="현재 비밀번호"
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
            style={{
              width: "100%",
              padding: "15px",
              marginBottom: "15px",
              borderRadius: "8px",
              border: "1px solid #4CAF50",
              backgroundColor: "#ffffff",
              color: "#333",
              transition: "border 0.3s",
            }}
            onFocus={(e) =>
              (e.currentTarget.style.border = "1px solid #66BB6A")
            }
            onBlur={(e) => (e.currentTarget.style.border = "1px solid #4CAF50")}
          />
          <input
            type="password"
            placeholder="새 비밀번호"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            style={{
              width: "100%",
              padding: "15px",
              marginBottom: "20px",
              borderRadius: "8px",
              border: "1px solid #4CAF50",
              backgroundColor: "#ffffff",
              color: "#333",
              transition: "border 0.3s",
            }}
            onFocus={(e) =>
              (e.currentTarget.style.border = "1px solid #66BB6A")
            }
            onBlur={(e) => (e.currentTarget.style.border = "1px solid #4CAF50")}
          />
          <button
            className="btn btn-primary"
            onClick={changePassword}
            style={{
              backgroundColor: "#4CAF50",
              color: "#fff",
              padding: "12px 20px",
              border: "none",
              borderRadius: "5px",
              cursor: "pointer",
              fontSize: "1.2rem",
              width: "100%",
              marginBottom: "10px",
              transition: "background-color 0.3s, transform 0.2s",
            }}
            onMouseOver={(e) => {
              e.currentTarget.style.backgroundColor = "#388E3C";
              e.currentTarget.style.transform = "scale(1.05)";
            }}
            onMouseOut={(e) => {
              e.currentTarget.style.backgroundColor = "#4CAF50";
              e.currentTarget.style.transform = "scale(1)";
            }}
          >
            비밀번호 변경
          </button>
          <button
            className="btn btn-secondary"
            onClick={() => setIsPasswordModalOpen(false)}
            style={{
              padding: "12px 20px",
              border: "none",
              borderRadius: "5px",
              backgroundColor: "#ccc",
              cursor: "pointer",
              fontSize: "1.2rem",
              width: "100%",
              transition: "background-color 0.3s",
            }}
            onMouseOver={(e) => {
              e.currentTarget.style.backgroundColor = "#bbb";
            }}
            onMouseOut={(e) => {
              e.currentTarget.style.backgroundColor = "#ccc";
            }}
          >
            취소
          </button>
        </Modal>
        <Modal
          isOpen={isDeleteModalOpen}
          onRequestClose={() => setIsDeleteModalOpen(false)}
          style={{
            content: {
              top: "50%",
              left: "50%",
              right: "auto",
              bottom: "auto",
              marginRight: "-50%",
              transform: "translate(-50%, -50%)",
              padding: "30px",
              border: "2px solid #ff1a1a",
              borderRadius: "15px",
              backgroundColor: "#333",
              color: "#ff4d4d",
              boxShadow: "0 6px 20px rgba(255, 0, 0, 0.7)",
            },
          }}
          ariaHideApp={false}
        >
          <h2 style={{ textAlign: "center", marginBottom: "20px" }}>
            ⚠️ 비밀번호 확인 ⚠️
          </h2>
          <p style={{ textAlign: "center", marginBottom: "20px" }}>
            이 작업은 <strong>되돌릴 수 없습니다!</strong>
            <br />
            정말로 회원 탈퇴를 원하신다면 비밀번호를 입력하세요.
          </p>
          <input
            type="password"
            placeholder="비밀번호를 입력하세요"
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
            style={{
              width: "100%",
              padding: "15px",
              marginBottom: "20px",
              borderRadius: "5px",
              border: "2px solid #ff1a1a",
              backgroundColor: "#444",
              color: "#fff",
            }}
          />
          <button
            className="btn btn-primary"
            onClick={quit}
            style={{
              backgroundColor: "#ff4d4d",
              color: "#fff",
              padding: "10px 20px",
              border: "none",
              borderRadius: "5px",
              cursor: "pointer",
              fontSize: "1.2rem",
              width: "100%",
              marginBottom: "10px",
              transition: "background-color 0.3s",
            }}
            onMouseOver={(e) => {
              e.currentTarget.style.backgroundColor = "#ff1a1a";
            }}
            onMouseOut={(e) => {
              e.currentTarget.style.backgroundColor = "#ff4d4d";
            }}
          >
            탈퇴하기
          </button>
          <button
            className="btn btn-secondary"
            onClick={() => setIsDeleteModalOpen(false)}
            style={{
              padding: "10px 20px",
              border: "none",
              borderRadius: "5px",
              backgroundColor: "#ccc",
              cursor: "pointer",
              fontSize: "1.2rem",
              width: "100%",
            }}
          >
            취소
          </button>
        </Modal>
      </div>
    </div>
  );
}
