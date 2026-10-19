import { Outlet } from "react-router-dom";
import Sidebar from "../components/Sidebar";

export default function AdminLayout() {
    return (
        <div className="min-h-screen bg-background">
            <Sidebar />
            <main className="lg:pl-64">
                <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:px-10">
                    <Outlet />
                </div>
            </main>
        </div>
    );
}