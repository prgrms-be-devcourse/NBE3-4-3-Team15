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
                <button className="btn btn-primary mt-2">로그인</button>
              </td>
              <td style={{ textAlign: "end" }}>
                <button className="btn btn-primary mt-2">회원가입</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}
