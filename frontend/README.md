# SecureAuth — Account Management Module

A production-ready Account Management module for the SecureAuth IAM platform, built with React 19, Vite, Tailwind CSS, and shadcn/ui.

## Stack

- **React 19** + **Vite** — app shell and build tooling
- **React Router 6** — routing, with lazy-loaded pages
- **Tailwind CSS** + **shadcn/ui** (New York style, neutral base) — UI primitives
- **React Hook Form** + **Zod** — form state and validation
- **Axios** — HTTP client with JWT attachment, refresh-token handling, and global error toasts
- **Lucide React** — icons

## Getting started

```bash
npm install
cp .env.example .env
npm run dev
```

The app boots straight into `/account` with **realistic mock data** (see `VITE_USE_MOCKS` below), so you can click through Overview, Profile, Security, and Activity Logs immediately — no backend required.

## Connecting the real backend

1. Set `VITE_API_BASE_URL` in `.env` to your API's base URL (e.g. `https://api.secureauth.example.com/api`).
2. Set `VITE_USE_MOCKS=false`.
3. Implement the following endpoints (contracts already match `src/features/account/services/accountApi.js`):

| Method | Path | Purpose |
| --- | --- | --- |
| `GET` | `/users/me` | Fetch the current user's profile |
| `PUT` | `/users/me` | Update `name`, `phone`, `bio` |
| `PUT` | `/users/change-password` | Change password (`currentPassword`, `newPassword`) |
| `GET` | `/users/security` | Security snapshot (verification, role, lock state, etc.) |
| `GET` | `/activity?page=&size=&search=&category=` | Paginated activity log, `hasMore`-based infinite scroll |
| `POST` | `/auth/refresh` | Refresh an expired access token (`refreshToken` in, `accessToken`/`refreshToken` out) |

Tokens are read from/written to `localStorage` via `src/lib/axiosClient.js`'s `tokenStorage` helper — swap that out if you'd rather use httpOnly cookies.

## Project structure

```
src/
├── components/ui/         shadcn/ui primitives (Button, Card, Form, Dialog, Sheet, Tabs, Select, Toast, ...)
├── components/             Spinner, EmptyState — small shared pieces with no shadcn equivalent
├── hooks/                  useDebounce, useInfiniteScroll — generic, reusable outside this feature
├── lib/                    cn() helper, Axios client + interceptors
├── routes/                 Router, NotFoundPage, LoginPlaceholder
└── features/account/
    ├── pages/              AccountLayout, OverviewPage, ProfilePage, SecurityPage, ActivityPage
    ├── components/         Sidebar, AccountCard, ProfileForm, PasswordForm, ActivityTimeline
    ├── services/           accountApi.js (real endpoints) + mockData.js (dev-mode fallback)
    ├── hooks/               useProfile, useChangePassword, useSecurityInfo, useActivityLogs
    ├── schemas/             Zod schemas for the profile and password forms
    └── types/               JSDoc typedefs (User, SecurityInfo, ActivityLog)
```

## Notes

- **Password strength**: enforced both in the Zod schema (`schemas/passwordSchema.js`) and shown live via a shared rule list, so the meter and the validation can never disagree.
- **Infinite scroll**: `useInfiniteScroll` wraps an `IntersectionObserver`; `useActivityLogs` guards against stale responses if you change the search/filter mid-request.
- **Sidebar**: a single component renders both the fixed desktop rail and the mobile `Sheet` drawer, so nav items only need to be defined once.
- **Profile picture upload** currently previews the selected file locally (`ProfileForm.jsx`) — wire `onFileSelect` to your image upload endpoint when one exists, since the spec's `PUT /users/me` doesn't currently accept an image.
- `/login` is a placeholder page (redirect target for logout / expired sessions) — swap it for SecureAuth's real authentication flow.
