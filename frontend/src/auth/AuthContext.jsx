import React, { createContext, useState, useEffect } from "react";
import api from "../api/api";
import { useNavigate } from "react-router-dom";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem("user")); } catch { return null; }
  });
  const [token, setToken] = useState(() => localStorage.getItem("token"));
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (token) localStorage.setItem("token", token);
    else localStorage.removeItem("token");
  }, [token]);

  useEffect(() => {
    if (user) localStorage.setItem("user", JSON.stringify(user));
    else localStorage.removeItem("user");
  }, [user]);

  const login = async (credentials) => {
    setLoading(true);
    try {
      const resp = await api.post("/auth/login", credentials);
      const { token: t, user: u } = resp.data;
      setToken(t);
      setUser(u || null);
      setLoading(false);
      return { ok: true };
    } catch (err) {
      setLoading(false);
      const message = err?.response?.data?.message || err.message || "Login failed";
      return { ok: false, message };
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    navigate("/login");
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}
