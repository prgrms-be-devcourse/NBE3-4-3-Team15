"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import client from "@/lib/client";

export default function ChangePassword() {
    const router = useRouter();
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    const handleChangePassword = async () => {
        if (newPassword !== confirmPassword) {
            setErrorMessage("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
            return;
        }

        try {
            await client.PUT("/members/mine/password", {
                body: { currentPassword, newPassword },
            });

            alert("비밀번호가 변경되었습니다.");
            router.push("/member/mine"); // 비밀번호 변경 후 내 정보 페이지로 이동
        } catch (error) {
            console.error("비밀번호 변경 실패:", error);
            setErrorMessage("비밀번호 변경에 실패하였습니다. 다시 시도해주세요.");
        }
    };

    return (
        <div className="container">
            <h2>비밀번호 변경</h2>
            {errorMessage && <p className="error">{errorMessage}</p>}
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
            <button onClick={handleChangePassword}>비밀번호 변경</button>
            <button className="cancel" onClick={() => router.push("/member/mine")}>취소</button>

            <style jsx>{`
                .container {
                    max-width: 400px;
                    margin: 50px auto;
                    padding: 20px;
                    display: flex;
                    flex-direction: column;
                    gap: 10px;
                    font-family: Arial, sans-serif;
                }
                .error {
                    color: red;
                    font-size: 14px;
                }
                input {
                    padding: 10px;
                    border: 1px solid #ccc;
                    border-radius: 5px;
                }
                button {
                    padding: 10px;
                    background-color: #007bff;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                }
                button.cancel {
                    background-color: #ccc;
                    margin-top: 5px;
                }
                button:hover {
                    background-color: #0056b3;
                }
                button.cancel:hover {
                    background-color: #999;
                }
            `}</style>
        </div>
    );
}
