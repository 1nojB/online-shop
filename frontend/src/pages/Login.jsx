import React, { useState } from "react";
import useAuth from "../auth/useAuth";
import { useNavigate, useLocation } from "react-router-dom";

export default function Login() {
  const { login, loading } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ username: "", password: "" });
  const [error, setError] = useState(null);

  const from = location.state?.from?.pathname || "/";

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    if (!form.username || !form.password) {
      setError("Username and password are required");
      return;
    }
    const res = await login({ username: form.username, password: form.password });
    if (res.ok) {
      navigate(from, { replace: true });
    } else {
      setError(res.message || "Login failed");
    }
  };

  return (
    <div style={{ maxWidth: 420, margin: "3rem auto", padding: 20, border: "1px solid #eee" }}>
      <h2>Login</h2>
      {error && <div style={{ color: "red", marginBottom: 8 }}>{error}</div>}
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 8 }}>
          <label>Username</label><br />
          <input name="username" value={form.username} onChange={handleChange} />
        </div>
        <div style={{ marginBottom: 8 }}>
          <label>Password</label><br />
          <input name="password" type="password" value={form.password} onChange={handleChange} />
        </div>
        <button type="submit" disabled={loading}>
          {loading ? "Logging in..." : "Log in"}
        </button>
      </form>
    </div>
  );
}
