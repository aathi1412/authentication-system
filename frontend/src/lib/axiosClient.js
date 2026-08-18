import {toast} from "@/components/ui/use-toast";
import axios from "axios";
import PATHS from "../routes/paths"

const BASE_URL = "http://localhost:8080/api";
/** Simple wrapper around localStorage so token storage has one seam to swap out later. */
export const tokenStorage = {
  getAccessToken: () => localStorage.getItem("sa_access_token"),
  getUserResponse: () => localStorage.getItem("userResponse"),

  setTokens: ({ accessToken, userResponse }) => {
    if (accessToken) localStorage.setItem("sa_access_token", accessToken);
    if (userResponse) localStorage.setItem("userResponse", userResponse);
  },

  clear: () => {
    localStorage.removeItem("sa_access_token");
    localStorage.removeItem("userResponse");
  },
};

export const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  withCredentials: true,
});

// ---- Request interceptor: attach the JWT to every outgoing request ----
apiClient.interceptors.request.use(
  (config) => {
    const token = tokenStorage.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ---- Response interceptor: refresh expired tokens once, then surface errors globally ----
let isRefreshing = false;
let pendingQueue = [];

function flushQueue(error, token = null) {
  pendingQueue.forEach(({ resolve, reject }) => {
    if (error) reject(error);
    else resolve(token);
  });
  pendingQueue = [];
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const status = error.response?.status;

    // Attempt a single silent token refresh on 401s (skip the refresh endpoint itself).
      const isAuthRequest = (url = "") =>
          url.includes("/auth/login") ||
          url.includes("/auth/register") ||
          url.includes("/auth/refresh");

    if (status === 401 &&
        !originalRequest?._retry &&
        !isAuthRequest(originalRequest?.url)) { //!originalRequest?.url?.includes("/auth/refresh")

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          pendingQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return apiClient(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {

        const { data } = await apiClient.post(`/auth/refresh`);

        tokenStorage.setTokens({
          accessToken: data.accessToken,
        });
        flushQueue(null, data.accessToken);

        originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        flushQueue(refreshError, null);
        tokenStorage.clear();
        toast({
          variant: "destructive",
          title: "Session expired",
          description: "Please sign in again to continue.",
        });
        window.location.assign(PATHS.AUTH.LOGIN);
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    // Global error handling for everything else.
    const message =
      error.response?.data?.message ||
      error.message ||
      "Something went wrong. Please try again.";

    if (status !== 401) {
      toast({
        variant: "destructive",
        title: status ? `Error ${status}` : "Network error",
        description: message,
      });
    }

    return Promise.reject(error);
  }
);

export default apiClient;
