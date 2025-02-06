import { Margarine } from "next/font/google";
import Image from "next/image";

export default function Home() {
  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <div>
        <table>
          <tbody>
            <tr>
              <td>ID</td>
              <td>
                <input type="text" className="border rounded" />
              </td>
            </tr>
            <tr>
              <td>Password</td>
              <td>
                <input type="password" className="border rounded" />
              </td>
            </tr>
            <tr>
              <td>
                <button className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg px-4 py-2 me-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 focus:outline-none dark:focus:ring-blue-800">
                  로그인
                </button>
              </td>
              <td style={{ textAlign: "end" }}>
                <button className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg px-4 py-2 me-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 focus:outline-none dark:focus:ring-blue-800">
                  회원가입
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}
