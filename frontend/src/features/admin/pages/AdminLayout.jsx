import Sidebar from '../components/Sidebar'

function AdminLayout() {
    return (
        <div>
            <Sidebar />
            <main>
                {/*<Outlet />*/}
            </main>
        </div>
    );
}

export default AdminLayout;