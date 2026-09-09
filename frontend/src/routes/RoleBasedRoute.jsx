import {Navigate} from "react-router-dom";
import PATHS from "./paths";

function RoleBasedRoute({ children, allowedRoles }) {
    const userResponse = JSON.parse(
        localStorage.getItem("userResponse") || "null"
    );

    const role = userResponse?.role;

    if (!role) {
        return <Navigate to={PATHS.AUTH.LOGIN} replace />;
    }

    if (!allowedRoles.includes(role)) {
        if (role === "ADMIN" || role === "SUPER_ADMIN") {
            return <Navigate to="/admin" replace />;
        }

        return <Navigate to={PATHS.USER.HOME} replace />;
    }

    return children;
}

export default RoleBasedRoute;