import { Outlet } from "react-router-dom";

import { Sidebar } from "@/features/account/components/Sidebar";

/**
 * Shell for every /account/* route: renders the sidebar (fixed on desktop,
 * a drawer on mobile) and an Outlet for the active page.
 */
export default function AccountLayout() {
  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <main className="lg:pl-64">
        <div className="mx-auto max-w-4xl px-4 py-8 sm:px-6 lg:px-10">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
