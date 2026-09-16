import {NavLink} from "react-router-dom";

function LogOutButton(){

}
function Sidebar() {
    return (
        <>
            <aside>
                <BrandMark />

                <nav>
                    <NavLink to="/admin">
                        Dashboard
                    </NavLink>

                    <NavLink to="/admin/users">
                        Users
                    </NavLink>

                    <NavLink to="/admin/activity">
                        Activity
                    </NavLink>

                    <NavLink to="/admin/settings">
                        Settings
                    </NavLink>
                </nav>

                <LogOutButton/>
            </aside>
        </>
    )
}

export default Sidebar;