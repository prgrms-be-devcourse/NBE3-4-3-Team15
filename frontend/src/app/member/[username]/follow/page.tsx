"use client";

import React, { useEffect, useState } from "react";
import axios from "axios";

interface FollowResponseDto {
    username: string;
    nickname: string;
    followerCount: number;
    followingCount: number;
}

const FollowingsPage = () => {
    const [followings, setFollowings] = useState<FollowResponseDto[]>([]);
    const [followers, setFollowers] = useState<FollowResponseDto[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const username = localStorage.getItem("username");

    if (!username) {
        return <div style={styles.message}>로그인 후 이용해 주세요.</div>;
    }

    // 팔로잉 목록을 가져오는 함수
    useEffect(() => {
        const fetchFollowings = async () => {
            try {
                const response = await axios.get(`/members/${username}/followings`);
                setFollowings(response.data.data);
            } catch (err) {
                setError("팔로잉 목록을 불러오는 데 실패했습니다.");
            }
        };

        fetchFollowings();
    }, [username]);

    // 팔로워 목록을 가져오는 함수
    useEffect(() => {
        const fetchFollowers = async () => {
            try {
                const response = await axios.get(`/members/${username}/followers`);
                setFollowers(response.data.data);
            } catch (err) {
                setError("팔로워 목록을 불러오는 데 실패했습니다.");
            } finally {
                setLoading(false);
            }
        };

        fetchFollowers();
    }, [username]);

    if (loading) return <div style={styles.loading}>로딩 중...</div>;
    if (error) return <div style={styles.error}>{error}</div>;

    return (
        <div style={styles.container}>
            <div style={styles.listsContainer}>
                {/* 팔로잉 목록 */}
                <div style={styles.followingContainer}>
                    <h2 style={styles.subTitle}>팔로잉 목록</h2>
                    {followings.length === 0 ? (
                        <p style={styles.noFollowings}>팔로잉한 사용자가 없습니다.</p>
                    ) : (
                        <ul style={styles.followingsList}>
                            {followings.map((following) => (
                                <li key={following.username} style={styles.followingItem}>
                                    <div style={styles.followingInfo}>
                                        <h3 style={styles.nickname}>{following.nickname}</h3>
                                        <p style={styles.username}>@{following.username}</p>
                                        <div style={styles.followStats}>
                                            <p>팔로워: {following.followerCount}</p>
                                            <p>팔로잉: {following.followingCount}</p>
                                        </div>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>

                {/* 팔로워 목록 */}
                <div style={styles.followerContainer}>
                    <h2 style={styles.subTitle}>팔로워 목록</h2>
                    {followers.length === 0 ? (
                        <p style={styles.noFollowings}>팔로워가 없습니다.</p>
                    ) : (
                        <ul style={styles.followersList}>
                            {followers.map((follower) => (
                                <li key={follower.username} style={styles.followingItem}>
                                    <div style={styles.followingInfo}>
                                        <h3 style={styles.nickname}>{follower.nickname}</h3>
                                        <p style={styles.username}>@{follower.username}</p>
                                        <div style={styles.followStats}>
                                            <p>팔로워: {follower.followerCount}</p>
                                            <p>팔로잉: {follower.followingCount}</p>
                                        </div>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>
        </div>
    );
};

const styles = {
    container: {
        maxWidth: "1000px",
        margin: "0 auto",
        padding: "20px",
        fontFamily: "'Arial', sans-serif",
    },
    listsContainer: {
        display: "flex",
        flexDirection: "row",
        justifyContent: "space-between",
        gap: "20px",
        marginTop: "20px",
    },
    followingContainer: {
        flex: "1",
        backgroundColor: "#f0f8ff",
        padding: "20px",
        borderRadius: "8px",
        boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
    },
    followerContainer: {
        flex: "1",
        backgroundColor: "#f8f0ff",
        padding: "20px",
        borderRadius: "8px",
        boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
    },
    subTitle: {
        fontSize: "1.5rem",
        fontWeight: "600",
        color: "#333",
        marginBottom: "10px",
    },
    noFollowings: {
        textAlign: "center",
        fontSize: "1.2rem",
        color: "#666",
    },
    followingsList: {
        listStyle: "none",
        padding: "0",
        margin: "0",
    },
    followersList: {
        listStyle: "none",
        padding: "0",
        margin: "0",
    },
    followingItem: {
        backgroundColor: "white",
        marginBottom: "15px",
        padding: "15px",
        borderRadius: "8px",
        boxShadow: "0 2px 5px rgba(0, 0, 0, 0.1)",
        display: "flex",
        flexDirection: "column",
    },
    followingInfo: {
        display: "flex",
        flexDirection: "column",
    },
    nickname: {
        fontSize: "1.5rem",
        color: "#333",
        marginBottom: "5px",
    },
    username: {
        fontSize: "1rem",
        color: "#888",
        marginBottom: "10px",
    },
    followStats: {
        display: "flex",
        gap: "20px",
        fontSize: "0.9rem",
        color: "#555",
    },
    loading: {
        textAlign: "center",
        fontSize: "1.2rem",
        color: "#007bff",
        marginTop: "20px",
    },
    error: {
        textAlign: "center",
        fontSize: "1.2rem",
        color: "#f44336",
        marginTop: "20px",
    },
    message: {
        textAlign: "center",
        fontSize: "1.2rem",
        color: "#f44336",
        marginTop: "20px",
    },
};

export default FollowingsPage;


