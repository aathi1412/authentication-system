import { lazy, Suspense } from "react";
import { createBrowserRouter, Navigate } from "react-router-dom";

import { Spinner } from "@/components/Spinner";

// Lazy-loaded so each account page ships as its own chunk.
const AccountLayout = lazy(() => import("@/features/account/pages/AccountLayout"));
const OverviewPage = lazy(() => import("@/features/account/pages/OverviewPage"));
const ProfilePage = lazy(() => import("@/features/account/pages/ProfilePage"));
const SecurityPage = lazy(() => import("@/features/account/pages/SecurityPage"));
const ActivityPage = lazy(() => import("@/features/account/pages/ActivityPage"));
const LoginPlaceholder = lazy(() => import("@/routes/LoginPlaceholder"));
const NotFoundPage = lazy(() => import("@/routes/NotFoundPage"));

function PageFallback() {
  return (
    <div className="flex min-h-[50vh] items-center justify-center">
      <Spinner size={24} className="text-muted-foreground" />
    </div>
  );
}

function withSuspense(element) {
  return <Suspense fallback={<PageFallback />}>{element}</Suspense>;
}

export const router = createBrowserRouter([
  { path: "/", element: <Navigate to="/account" replace /> },
  { path: "/login", element: withSuspense(<LoginPlaceholder />) },
  {
    path: "/account",
    element: withSuspense(<AccountLayout />),
    children: [
      { index: true, element: withSuspense(<OverviewPage />) },
      { path: "profile", element: withSuspense(<ProfilePage />) },
      { path: "security", element: withSuspense(<SecurityPage />) },
      { path: "activity", element: withSuspense(<ActivityPage />) },
    ],
  },
  { path: "*", element: withSuspense(<NotFoundPage />) },
]);
