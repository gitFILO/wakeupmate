"use client";

import { useState, useEffect } from "react";

export default function Home() {
  const [user, setUser] = useState(null);
  
  const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;
  
  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/v1/users/profile`, {
          credentials: "include", // 쿠키 포함
        });
        console.log(API_BASE_URL)
        if (response.ok) {
          const data = await response.json();

          console.log(data.username, data.profileImageUrl);
          setUser({
            username: data.username,
            profileImageUrl: data.profileImageUrl,
          });
          
        }
      } catch (error) {
        console.error("Failed to fetch user profile:", error);
      }
    };

    fetchUserProfile();
  }, []);

  // 카카오 로그인 핸들러
  const handleKakaoLogin = () => {
    window.location.href = `${API_BASE_URL}/oauth2/authorization/kakao`;
  };

  // 로그아웃 핸들러
  const handleLogout = async () => {
    try {
      await fetch(`${API_BASE_URL}/logout`, {
        method: "POST",
        credentials: "include", // 쿠키 포함
      });
      setUser(null); // 사용자 정보 초기화
    } catch (error) {
      console.error("Failed to logout:", error);
    }
  };

  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <h1 className="text-2xl font-bold">Welcome!</h1>

      {/* 로그인 상태에 따라 다른 UI 렌더링 */}
      {user ? (
        <div className="flex flex-col items-center gap-4">
          <img
            src={user.profileImageUrl}
            alt="Profile"
            className="w-24 h-24 rounded-full shadow-lg"
          />
          <p className="text-lg font-medium">안녕하세요, {user.username}님!</p>
          <button
            onClick={handleLogout}
            className="bg-red-500 hover:bg-red-600 text-white font-semibold py-2 px-4 rounded-lg shadow-md transition-all"
          >
            로그아웃
          </button>
        </div>
      ) : (
        <button
          onClick={handleKakaoLogin}
          className="bg-yellow-400 hover:bg-yellow-500 text-black font-semibold py-2 px-4 rounded-lg shadow-md transition-all"
        >
          카카오 로그인
        </button>
      )}
    </div>
  );
}
