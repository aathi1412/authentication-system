import apiClient from "./axiosClient";

export const registerUser = (data) => apiClient.post("/auth/register", data);
export const loginUser = (data) => apiClient.post("/auth/login", data);
export const logoutUser = () => apiClient.post("/auth/logout");
export const forgotPassword = (data) => apiClient.post("/auth/forgot-password", data);
export const resetPassword = (token, password) => apiClient.post("/auth/reset-password", { token, password });
export const resendVerificationEmail = (email) => apiClient.post("/auth/resend-verification-email", { email });
export const activateAccount = (token) => apiClient.get(`/auth/activate-account?token=${encodeURIComponent(token)}`);