import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { cookies } from "next/headers";
import client from "./lib/client";
export async function middleware(req: NextRequest) {
  const meResponse = await client.GET("/members/mine", {
    headers: {
      cookie: (await cookies()).toString(),
    },
  });

  console.log(`meResponse: ${meResponse}`);

  if (meResponse.response.headers.get("Set-Cookie")) {
    const cookieValue = meResponse.response.headers.get("Set-Cookie")!;

    console.log(`cookieValue: ${cookieValue}`);


    // 아래 코드는 이 middleware 에서 fetch를 날려서 갱신된 accessToken 쿠키를 받아왔을 때 다음과 같은 역할을 한다.
    // 역할 : Next.js 에서 이번 요청에 대해서 응답을 완료했을 때 브라우저에게 새 accessToken 쿠키를 반영하도록 한다.
    (await cookies()).set({
      name: cookieValue.split("=")[0],
      value: cookieValue.split("=")[1].split(";")[0],
    });
  }

  return NextResponse.next({
    headers: {
      // 아래 코드는 이 middleware 에서 fetch를 날려서 갱신된 accessToken 쿠키를 받아왔을 때 다음과 같은 역할을 한다.
      // 역할 : 이 미들웨어 이후에 수행되는 fetch(src/app/layout.tsx, src/app/member/me/page.tsx) 에서 새 accessToken 쿠키가 반영된다.
      cookie: (await cookies()).toString(),
    },
  });
}

export const config = {
  // 아래 2가지 경우에는 middleware를 실행하지 않도록 세팅
  // api 로 시작하거나 하는 요청 : /api/~~~
  // 정적 파일 요청 : /~~~.jpg, /~~~.png, /~~~.css, /~~~.js
  // PS. 여기서 말하는 api 로 시작하는 요청은 백엔드 API 서버로의 요청이 아니라 Next.js 고유의 API 서버로의 요청이다.
  // PS. 우리는 현재 이 기능을 사용하고 있지 않다.
  matcher: "/((?!.*\\.|api\\/).*)",
};