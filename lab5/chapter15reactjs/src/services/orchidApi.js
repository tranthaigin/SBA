import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL;

const api = axios.create({
  baseURL: API_URL,
  headers: { "Content-Type": "application/json" },
});

export const getOrchids = () => api.get("/");
export const getOrchid = (id) => api.get(`/${id}`);
export const createOrchid = (payload) => api.post("/", payload);
export const updateOrchid = (id, payload) => api.put(`/${id}`, payload);
export const deleteOrchid = (id) => api.delete(`/${id}`);

export default api;
