import {Link} from "react-router-dom";

export function AuthSwitch({
                               path,
                               doAction,
                               children,
                               className = ""
                           }) {
    return (
        <div className="mt-4 text-gray-600 text-[13px] text-center">
            <p className={`text-sm ${className}`}>
                {children}
                <Link
                    to={path}
                    className="text-black underline decoration-2 hover:underline-offset-4 font-semibold active:text-gray-500"
                >
                    {doAction}
                </Link>
            </p>
        </div>
    )
}

export default AuthSwitch