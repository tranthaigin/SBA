import { createContext, useContext, useState, useEffect } from "react";
import axios from "axios";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem("hotel_user");
    return saved ? JSON.parse(saved) : null;
  });

  const [token, setToken] = useState(() => {
    return localStorage.getItem("hotel_token") || "";
  });

  useEffect(() => {
    if (token) {
      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    } else {
      delete axios.defaults.headers.common["Authorization"];
    }
  }, [token]);

  const login = async (email, password) => {
    const res = await axios.post("/api/auth/login", { email, password });
    const { token: jwt, ...userInfo } = res.data;
    setToken(jwt);
    setUser(userInfo);
    localStorage.setItem("hotel_token", jwt);
    localStorage.setItem("hotel_user", JSON.stringify(userInfo));
    return userInfo;
  };

  const register = async (customerData) => {
    const res = await axios.post("/api/auth/register", customerData);
    const { token: jwt, ...userInfo } = res.data;
    setToken(jwt);
    setUser(userInfo);
    localStorage.setItem("hotel_token", jwt);
    localStorage.setItem("hotel_user", JSON.stringify(userInfo));
    return userInfo;
  };

  const logout = () => {
    setUser(null);
    setToken("");
    localStorage.removeItem("hotel_token");
    localStorage.removeItem("hotel_user");
    delete axios.defaults.headers.common["Authorization"];
  };

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
