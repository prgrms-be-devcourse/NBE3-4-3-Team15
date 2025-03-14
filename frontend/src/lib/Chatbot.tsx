"use cliendt";

import { useEffect, useState } from "react";
import client from "./client";

export default function Chatbot() {
  const [messages, setMessages] = useState([
    {
      role: "bot",
      content: "안녕하세요! 어떤 책을 추천해 드릴까요?",
    },
  ]);
  const [input, setInput] = useState("");
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  async function getAnswer(question) {
    const response = client.GET(`/chatbot?message=${question}`);
    setInput("");
    return response;
  }

  async function getQuestion(question) {
    const userMessage = { role: "user", content: input };
    setMessages((prev) => [...prev, userMessage]);
    return "";
  }

  const changeOpen = async (e) => {
    setIsOpen(!isOpen);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!input.trim()) return;

    setIsLoading((check) => !check);

    // Simulate bot response
    const [message, response] = await Promise.all([
      getQuestion(input),
      getAnswer(input),
    ]);

    setIsLoading((check) => !check);

    const botMessage = {
      role: "bot",
      content: response.data.data.message,
    };
    setTimeout(() => {
      setMessages((prev) => [...prev, botMessage]);
    }, 500);
  };

  return (
    <div
      style={{
        position: "fixed",
        zIndex: 1000,
        right: "10px",
        bottom: "20px",
      }}
    >
      {isOpen && (
        <div className="max-w-md mx-auto p-4 bg-white rounded-2xl shadow-lg">
          <h1 className="text-xl font-bold mb-4">Chatbot</h1>
          <div className="h-80 overflow-y-auto border p-2 rounded-md mb-4">
            {messages.map((msg, index) => (
              <div
                key={index}
                className={`mb-2 ${
                  msg.role === "user" ? "text-right" : "text-left"
                }`}
              >
                <div
                  style={{ whiteSpace: "pre-wrap", display: "inline-block" }}
                  className={`inline-block p-2 rounded-lg ${
                    msg.role === "user"
                      ? "bg-blue-500 text-white"
                      : "bg-gray-200"
                  }`}
                >
                  {msg.content}
                </div>
              </div>
            ))}
            {isLoading && (
              <div className="mb-2 text-left">
                <div
                  style={{ whiteSpace: "pre-wrap", display: "inline-block" }}
                  className={"inline-block p-2 rounded-lg bg-gray-200"}
                >
                  Loading...
                </div>
              </div>
            )}
          </div>
          <form onSubmit={handleSubmit} className="flex gap-2">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="메시지를 입력하세요..."
              className="flex-1 border rounded-lg p-2"
              style={{ width: "calc(100% - 84px)" }}
            />
            <button
              type="submit"
              className="bg-blue-500 text-white rounded-lg px-4"
            >
              전송
            </button>
          </form>
        </div>
      )}
      <div style={{ float: "right" }} onClick={changeOpen}>
        <img src="/image.png"></img>
      </div>
    </div>
  );
}
