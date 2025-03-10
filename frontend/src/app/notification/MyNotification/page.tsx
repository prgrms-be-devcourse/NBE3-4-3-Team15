"use client";

import React, { useEffect, useState } from "react";

const MyNotifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalNotificationCount, setTotalNotificationCount] = useState(0);

  const getNotifications = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/notification/myNotification?page=${page}&size=10`,
        {
          method: "GET",
          credentials: "include",
        }
      );
      if (!response.ok) {
        throw new Error("네트워크 응답 에러");
      }
      const data = await response.json();
      console.log("알림 데이터:", data);

      const notificationList = data.data.content;
      setNotifications(notificationList);
      setTotalPages(data.data.totalPages || 1);
    } catch (e) {
      console.error("알람데이터 요청 실패 :", e);
    }
  };

  const getNotificationsCount = async () => {
    try {
      const response = await fetch(
        "http://localhost:8080/notification/notificationCount",
        {
          method: "GET",
          credentials: "include",
        }
      );
      if (!response.ok) {
        throw new Error("네트워크 응답 에러");
      }
      const data = await response.json();
      setTotalNotificationCount(data.data);
      console.log("전체 알림 수:", data.data);
    } catch (e) {
      console.error("전체 알람 수 요청 실패 :", e);
    }
  };

  // 알림 삭제 함수
  const deleteNotification = async (notificationId) => {
    try {
      const response = await fetch(
        `http://localhost:8080/notification/${notificationId}`,
        {
          method: "DELETE",
          credentials: "include",
        }
      );
      if (!response.ok) {
        throw new Error("삭제 실패");
      }
      console.log("삭제 성공");
      await getNotifications();
      await getNotificationsCount();
    } catch (error) {
      console.error("삭제 요청 실패:", error);
    }
  };

  useEffect(() => {
    getNotifications();
  }, [page]);

  useEffect(() => {
    getNotificationsCount();
  }, []);

  const handlePrevPage = () => {
    if (page > 1) {
      setPage((prev) => prev - 1);
    }
  };

  const handleNextPage = () => {
    if (page < totalPages) {
      setPage((prev) => prev + 1);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-6 flex flex-col justify-center items-center">
      <h1 style={{ color: "black", marginBottom: "20px", fontSize: "20px" }}>
        총 알림 수: {totalNotificationCount}
      </h1>
      <div className="w-full">
        {notifications.length > 0 ? (
          notifications.map((notification) => (
            <div
              key={notification.id}
              className="max-w-4xl w-full bg-white p-8 rounded-lg shadow-lg mb-4"
            >
              <div className="flex justify-between items-center">
                <h3 className="text-lg font-bold text-black">
                  {notification.content}
                </h3>
                <div className="flex flex-col items-end">
                  <p className="text-gray-500">
                    {notification.create_At.substring(0, 19)}
                  </p>
                  <button
                    className="px-2 py-1 bg-red-500 hover:bg-red-600 text-white text-sm font-semibold rounded duration-150 mt-2"
                    onClick={() => deleteNotification(notification.id)}
                  >
                    삭제
                  </button>
                </div>
              </div>
            </div>
          ))
        ) : (
          <p>알림이 없습니다.</p>
        )}
      </div>
      <div className="mt-4 flex gap-4">
        <button
          onClick={handlePrevPage}
          className="px-4 py-2 bg-blue-500 text-white rounded disabled:opacity-50"
          disabled={page === 1}
        >
          Prev
        </button>
        <span className="text-black">
          Page {page} of {totalPages}
        </span>
        <button
          onClick={handleNextPage}
          className="px-4 py-2 bg-blue-500 text-white rounded disabled:opacity-50"
          disabled={page === totalPages}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default MyNotifications;
