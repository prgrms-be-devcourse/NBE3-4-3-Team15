"use client";

import React, { useEffect, useState } from "react";
import Navbar from "@/lib/Navbar";

const ClientLayout: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [accessToken, setAccessToken] = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    setAccessToken(token);
  }, []);

  return (
    <div>
      <Navbar accessToken={accessToken} setAccessToken={setAccessToken} />
      <main>{children}</main>
    </div>
  );
};

export default ClientLayout;
