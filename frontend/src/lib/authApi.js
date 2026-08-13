import apiClient from "./axiosClient";

export const registerUser = (data) => apiClient.post("/auth/register", data)
export const loginUser = (data) => apiClient.post("/auth/login", data)
export const forgotPassword = (data) => apiClient.post("/auth/forgot-password", data)
export const resetPassword = (token, password) => apiClient.post("/auth/reset-password", {token, password})