"use client";

import Navbar from "@/lib/Navbar";

const ClientLayout: React.FC<{
  children: React.ReactNode;
  accessToken: string | null;
}> = ({ children, accessToken }) => {
  console.log("accessToken = " + accessToken);
  return (
    <div>
      <Navbar accessToken={accessToken} />
      <main>{children}</main>
    </div>
  );
};

export default ClientLayout;
