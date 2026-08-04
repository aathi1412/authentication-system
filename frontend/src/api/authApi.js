import api from "./axios";

export const registerUser = (data) => api.post("/register", data)
export const loginUser = (data) => api.post("/login", data)
export const forgotPassword = (data) => api.post("/forgot-password", data)
export const resetPassword = (token, password) => api.post("/reset-password", {token, password})