import React from "react";
import useAuth from "../auth/useAuth";

export default function Home() {
  const { user, logout } = useAuth();
  return (
    <div style={{ padding: 20 }}>
      <h1>Welcome {user?.username || "User"}</h1>
      <button onClick={logout}>Logout</button>
    </div>
  );
}
