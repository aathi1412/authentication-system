import apiClient from "./axiosClient";

export const registerUser = (data) => apiClient.post("/register", data)
export const loginUser = (data) => apiClient.post("/login", data)
export const forgotPassword = (data) => apiClient.post("/forgot-password", data)
export const resetPassword = (token, password) => apiClient.post("/reset-password", {token, password})