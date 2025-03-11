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
    const [userProfile, setUserProfile] = useState<{ id:number | undefined, username : string, email: string; nickname: string; gender?: number; birth?: string; } | null>(null);
    const [isEditing, setIsEditing] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [, setIsPasswordModalOpen] = useState(false);
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [username, setUsername] = useState<string | null>(null); // ✅ 상태로 저장

    const router = useRouter();

    useEffect(() => {
        setUsername(localStorage.getItem("username")); // ✅ 클라이언트에서만 실행
        getUserProfile();
    }, []);

    const getUserProfile = async () => {
        try {
            const response = await client.GET("/members/mine");
            if (response.data?.data) {
                const { id, username, email, nickname, gender, birth } = response.data.data;
                setUserProfile({ id, username,  email, nickname, gender, birth });
            }
        } catch (error) {
            console.error("회원 정보 조회 실패:", error);
            alert("회원 정보 조회에 실패하였습니다.");
        }
    };


    const edit = () => setIsEditing(!isEditing);
    const goToFavoriteBooksPage = () => router.push("/book/favorite");

    const modify = async () => {
        if (userProfile) {
            try {
                await client.PUT("/members/mine", { body: userProfile });
                alert("프로필이 업데이트되었습니다.");
                setIsEditing(false);
                getUserProfile()
            } catch (error) {
                console.error("프로필 업데이트 실패:", error);
                alert("프로필 업데이트에 실패했습니다.");
            }
        }
    };

    const passwordEdit = () => window.location.href = "/member/mine/password";

    const changePassword = async () => {
        if (newPassword !== confirmPassword) {
            alert("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            return;
        }
        try {
            await client.PUT("/members/mine/password", { body: { currentPassword, newPassword } });
            alert("비밀번호가 변경되었습니다.");
            setIsPasswordModalOpen(false);
            setCurrentPassword(""); setNewPassword(""); setConfirmPassword("");
        } catch (error) {
            console.error("비밀번호 변경 실패:", error);
            alert("비밀번호 변경에 실패했습니다.");
        }
    };

    const quit = async () => {
        if (!confirm("정말 탈퇴하시겠습니까?")) return;
        try {
            const response = await client.DELETE("/members/mine", { body: { password: currentPassword } });
            if (response.response.ok) {
                alert("탈퇴 완료되었습니다.");
                setCurrentPassword("");
                window.location.href = "/";
            } else {
                alert("비밀번호가 맞지 않습니다.");
            }
        } catch (error) {
            console.error("회원 탈퇴 실패:", error);
            alert("탈퇴에 실패했습니다.");
        }
    };

    const goToFollowPage = () => {
        if (username) {
            router.push(`/member/${username}/follow`);
        } else {
            alert("유저 정보가 없습니다.");
        }
    };

    return (
        <div className="profile-container">
            {userProfile ? (
                <div className="profile-card">
                    <h2>내 프로필</h2>
                    <div className="profile-content">
                        <p><strong>Email:</strong> {isEditing ? <input type="text" value={userProfile.email} onChange={(e) => setUserProfile({ ...userProfile, email: e.target.value })} /> : userProfile.email}</p>
                        <p><strong>닉네임:</strong> {isEditing ? <input type="text" value={userProfile.nickname} onChange={(e) => setUserProfile({ ...userProfile, nickname: e.target.value })} /> : userProfile.nickname}</p>
                        <p><strong>성별:</strong> {isEditing ? <select value={userProfile.gender} onChange={(e) => setUserProfile({ ...userProfile, gender: Number(e.target.value) })}><option value={0}>남성</option><option value={1}>여성</option></select> : (userProfile.gender === 0 ? "남성" : "여성")}</p>
                        <p><strong>생년월일:</strong> {isEditing ? <input type="date" value={userProfile.birth} onChange={(e) => setUserProfile({ ...userProfile, birth: e.target.value })} /> : userProfile.birth}</p>
                    </div>

                    <div className="button-group">
                        {isEditing ? (
                            <>
                                <button className="save-btn" onClick={modify}>저장</button>
                                <button className="cancel-btn" onClick={edit}>취소</button>
                            </>
                        ) : (
                            <>
                                <button className="edit-btn" onClick={edit}>정보 수정</button>
                                <button className="password-btn" onClick={passwordEdit}>비밀번호 변경</button>
                                <button className="favorite-btn" onClick={goToFavoriteBooksPage}>찜한 도서 목록</button>
                                <button className="follow-btn" onClick={goToFollowPage}>팔로우 관리</button>
                                <button className="delete-btn" onClick={() => setIsDeleteModalOpen(true)}>회원 탈퇴</button>
                            </>
                        )}
                    </div>
                </div>
            ) : <p>로딩 중...</p>}

            <Modal isOpen={isDeleteModalOpen} onRequestClose={() => setIsDeleteModalOpen(false)}>
                <h2>회원 탈퇴</h2>
                <input type="password" placeholder="비밀번호" value={currentPassword} onChange={(e) => setCurrentPassword(e.target.value)} />
                <button className="delete-btn" onClick={quit}>탈퇴하기</button>
            </Modal>

            <style jsx>{`
                .profile-container {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                    background: linear-gradient(135deg, #667eea, #764ba2);
                }

                .profile-card {
                    background: white;
                    padding: 30px;
                    border-radius: 10px;
                    box-shadow: 0px 10px 30px rgba(0, 0, 0, 0.1);
                    width: 400px;
                    text-align: center;
                }

                h2 {
                    color: #333;
                }

                .profile-content p {
                    font-size: 18px;
                    margin: 10px 0;
                }

                .button-group {
                    display: flex;
                    flex-direction: column;
                    gap: 10px;
                }

                button {
                    padding: 10px 15px;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    transition: all 0.3s;
                    font-size: 16px;
                }

                .edit-btn {
                    background: #007bff;
                    color: white;
                }
                .edit-btn:hover {
                    background: #0056b3;
                }

                .password-btn {
                    background: #28a745;
                    color: white;
                }
                .password-btn:hover {
                    background: #218838;
                }

                .follow-btn { background: #007bff; color: white; }
                .follow-btn:hover { background: #0056b3; }

                .favorite-btn { background: #6c757d; color: white; }
                .favorite-btn:hover { background: #5a6268; }

                .delete-btn { background: #dc3545; color: white; }
                .delete-btn:hover { background: #c82333; }

                .save-btn {
                    background: #28a745;
                    color: white;
                }

                .cancel-btn {
                    background: #6c757d;
                    color: white;
                }

                .save-btn:hover {
                    background: #218838;
                }

                .cancel-btn:hover {
                    background: #5a6268;
                }
            `}</style>
        </div>
    );
}
