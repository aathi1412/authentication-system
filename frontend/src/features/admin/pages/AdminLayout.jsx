import {NavLink, Outlet} from "react-router-dom";

function AdminLayout() {
    return (
        <div>
            <aside>
                <h2>Admin Panel</h2>

                <nav>
                    <NavLink to="/admin">
                        Dashboard
                    </NavLink>

                    <NavLink to="/admin/users">
                        Users
                    </NavLink>
                </nav>
            </aside>

            <main>
                <Outlet />
            </main>
        </div>
    );
}

export default AdminLayout;