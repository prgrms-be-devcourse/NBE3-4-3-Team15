"use client";

import client from "@/lib/client";
import React, { useState, useEffect } from "react";
import Modal from "react-modal";
import type { components } from "@/lib/backend/schema";

type MineDto = components["schemas"]["MineDto"];

/**
 * 회원 프로필 조회, 수정, 탈퇴 페이지
 */
export default function Mine() {
    const [userProfile, setUserProfile] = useState<{
        email: string;
        nickname: string;
        gender?: number;
        birth?: string;
    } | null>(null);

    const [isEditing, setIsEditing] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState(""); // 비밀번호 확인 필드 추가

    const getUserProfile = async () => {
        try {
            const response = await client.GET("/members/mine");
            if (response.data?.data) {
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
        if (userProfile) {
            try {
                await client.PUT("/members/mine", { body: userProfile });
                alert("프로필이 업데이트되었습니다.");
                setIsEditing(false);
            } catch (error) {
                console.error("프로필 업데이트 실패", error);
                alert("프로필 업데이트에 실패했습니다.");
            }
        }
    };

    // 비밀번호 변경 모달 상태 변경
    const passwordEdit = () => {
        setIsPasswordModalOpen(!isPasswordModalOpen);
    };

    // 비밀번호 변경 요청
    const changePassword = async () => {
        if (newPassword !== confirmPassword) {
            alert("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            return;
        }

        try {
            await client.PUT("/members/mine/password", {
                body: { currentPassword, newPassword },
            });

            alert("비밀번호가 변경되었습니다.");
            setIsPasswordModalOpen(false);
            setCurrentPassword("");
            setNewPassword("");
            setConfirmPassword("");
        } catch (error) {
            console.error("비밀번호 변경에 실패하였습니다.", error);
            alert("비밀번호 변경에 실패하였습니다.");
        }
    };

    // 회원 탈퇴 요청
    const quit = async () => {
        try {
            if (!confirm("정말 탈퇴하시겠습니까?")) return;
            const response = await client.DELETE("/members/mine", {
                body: { password: currentPassword },
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
        <div className="profile-container">
            {userProfile ? (
                <table className="profile-table">
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
                        <td>Nickname</td>
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
                        <td>Gender</td>
                        <td>
                            {isEditing ? (
                                <select
                                    value={userProfile.gender}
                                    onChange={(e) =>
                                        setUserProfile({
                                            ...userProfile,
                                            gender: Number(e.target.value),
                                        })
                                    }
                                >
                                    <option value={1}>Male</option>
                                    <option value={2}>Female</option>
                                </select>
                            ) : (
                                (userProfile.gender === 1 ? "Male" : "Female") || "Not specified"
                            )}
                        </td>
                    </tr>
                    <tr>
                        <td>Birth Date</td>
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
                                    <div style={{ display: "flex", gap: "10px" }}>
                                        <button onClick={modify}>저장</button>
                                        <button onClick={edit}>취소</button>
                                    </div>
                                </>
                            ) : (
                                <div style={{ display: "flex", gap: "10px" }}>
                                    <button onClick={edit}>정보 수정</button>
                                    <button onClick={passwordEdit}>비밀번호 변경</button>
                                </div>
                            )}
                        </td>
                    </tr>
                    </tbody>
                </table>
            ) : (
                <p>로딩 중...</p>
            )}
            <button onClick={() => setIsDeleteModalOpen(true)} className="delete-button">
                회원 탈퇴
            </button>

            {/* 비밀번호 변경 모달 */}
            <Modal isOpen={isPasswordModalOpen} onRequestClose={() => setIsPasswordModalOpen(false)}>
                <h2>비밀번호 변경</h2>
                <input
                    type="password"
                    placeholder="현재 비밀번호"
                    value={currentPassword}
                    onChange={(e) => setCurrentPassword(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="새 비밀번호"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="새 비밀번호 확인"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                />
                <button onClick={changePassword}>비밀번호 변경</button>
            </Modal>

            {/* 탈퇴 모달 */}
            <Modal isOpen={isDeleteModalOpen} onRequestClose={() => setIsDeleteModalOpen(false)}>
                <h2>회원 탈퇴</h2>
                <input
                    type="password"
                    placeholder="비밀번호"
                    value={currentPassword}
                    onChange={(e) => setCurrentPassword(e.target.value)}
                />
                <button onClick={quit}>탈퇴하기</button>
            </Modal>

            <style jsx>{`
                .profile-container {
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                    font-family: Arial, sans-serif;
                }

                .profile-table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-bottom: 20px;
                }

                .profile-table td {
                    padding: 10px;
                    border: 1px solid #ccc;
                }

                .profile-table input,
                .profile-table select {
                    width: 100%;
                    padding: 8px;
                    margin: 5px 0;
                    border: 1px solid #ccc;
                    border-radius: 5px;
                }

                .delete-button {
                    background-color: red;
                    color: white;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 5px;
                    cursor: pointer;
                }

                .delete-button:hover {
                    background-color: darkred;
                }

                button {
                    padding: 10px 20px;
                    background-color: #007bff;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                }

                button:hover {
                    background-color: #0056b3;
                }
            `}</style>
        </div>
    );
}
