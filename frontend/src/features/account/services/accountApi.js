import {apiClient} from "@/lib/axiosClient";


/**
 * All account-related network calls in one place, kept separate from
 * components/hooks so the UI never talks to Axios directly.
 */
export const accountApi = {

  getProfile: () => apiClient.get("/users/me"),

  updateProfile: (payload) => apiClient.put("/users/me", payload),

  updateProfileImage: (formData) =>
    apiClient.post("/users/profile/image", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    }),

  changePassword: (payload) => 
    apiClient.put("/users/change-password", payload),

  getSecurityInfo: () =>
    apiClient.get("/users/security"),

  getActivityLogs: ({ page = 0, pageSize = 10, search = "", category = "ALL" }) =>
    apiClient.get("/users/activity", {
      params: {
        page,
        pageSize,
        search: search || undefined,
        category: category !== "ALL" ? category : undefined,
      },
    }),
};
