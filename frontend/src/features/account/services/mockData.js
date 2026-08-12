// Mock data used when VITE_USE_MOCKS is enabled (see .env.example). Lets the
// module run and demo end-to-end before the real IAM backend is wired up.
// Swap this out (or disable mocks) once /api/users and /api/activity are live.

const wait = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

let mockUser = {
  id: 1,
  name: "John Doe",
  email: "john@gmail.com",
  phone: "9876543210",
  bio: "Java Developer building identity platforms.",
  profileImage: "",
};

const mockSecurity = {
  emailVerified: true,
  role: "USER",
  accountLocked: false,
  failedAttempts: 1,
  lastLogin: "2026-07-25T09:14:00Z",
  createdAt: "2023-02-11T10:00:00Z",
};

const ACTIVITY_CATALOG = [
  {
    type: "LOGIN",
    title: "Logged in",
    description: "Login from Chrome on Windows",
    category: "Authentication",
  },
  {
    type: "PASSWORD_CHANGE",
    title: "Password changed",
    description: "Password updated from account settings",
    category: "Security",
  },
  {
    type: "PROFILE_UPDATE",
    title: "Profile updated",
    description: "Bio and phone number updated",
    category: "Profile",
  },
  {
    type: "EMAIL_VERIFIED",
    title: "Email verified",
    description: "Email address confirmed via verification link",
    category: "Security",
  },
  {
    type: "FAILED_LOGIN",
    title: "Failed login attempt",
    description: "Incorrect password entered from an unrecognized device",
    category: "Authentication",
  },
  {
    type: "PASSWORD_RESET",
    title: "Password reset requested",
    description: "Reset link sent to john@gmail.com",
    category: "Security",
  },
];

function buildMockActivity(total = 47) {
  const now = Date.now();
  return Array.from({ length: total }, (_, i) => {
    const template = ACTIVITY_CATALOG[i % ACTIVITY_CATALOG.length];
    return {
      id: total - i,
      ...template,
      createdAt: new Date(now - i * 5 * 60 * 60 * 1000).toISOString(),
    };
  });
}

const MOCK_ACTIVITY = buildMockActivity();

export const mockAccountApi = {
  async getProfile() {
    await wait(500);
    return { data: mockUser };
  },

  async updateProfile(payload) {
    await wait(700);
    mockUser = { ...mockUser, ...payload };
    return { data: mockUser };
  },

  async changePassword() {
    await wait(800);
    return { data: { message: "Password updated successfully" } };
  },

  async getSecurityInfo() {
    await wait(450);
    return { data: mockSecurity };
  },

  async getActivityLogs({ page, size, search, category }) {
    await wait(600);
    let items = MOCK_ACTIVITY;

    if (search) {
      const q = search.toLowerCase();
      items = items.filter(
        (item) =>
          item.title.toLowerCase().includes(q) ||
          item.description.toLowerCase().includes(q)
      );
    }

    if (category && category !== "ALL") {
      items = items.filter((item) => item.category === category);
    }

    const start = page * size;
    const paged = items.slice(start, start + size);

    return {
      data: {
        content: paged,
        totalElements: items.length,
        hasMore: start + size < items.length,
      },
    };
  },
};
