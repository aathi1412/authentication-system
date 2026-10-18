import {mockAccountApi} from "@/features/account/services/mockData";
import {apiClient} from "@/lib/axiosClient";

// Toggle with VITE_USE_MOCKS=false once the real IAM backend is available.
// Defaults to true so the module is demoable immediately after install.
const USE_MOCKS = import.meta.env.VITE_USE_MOCKS !== "false";

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
    USE_MOCKS
      ? mockAccountApi.changePassword(payload)
      : apiClient.put("/users/change-password", payload),

  getSecurityInfo: () =>
    USE_MOCKS
      ? mockAccountApi.getSecurityInfo()
      : apiClient.get("/users/security"),

  getActivityLogs: ({ page = 0, pageSize = 10, search = "", category = "ALL" }) =>
    USE_MOCKS
      ? mockAccountApi.getActivityLogs({ page, size: pageSize, search, category })
      : apiClient.get("/users/activity", {
          params: {
            page,
            pageSize,
            search: search || undefined,
            category: category !== "ALL" ? category : undefined,
          },
        }),
};
