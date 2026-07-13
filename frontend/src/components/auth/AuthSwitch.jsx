import {Link} from "react-router-dom";

export function AuthSwitch({
                               path,
                               doAction,
                               children,
                               className = ""}) {
    return(
        <div className="mt-4 text-gray-600 text-[13px] text-center">
            <p className={`text-sm ${className}`}>
                {children}
                <Link
                    to={path}
                    className="text-[#3172ed] font-semibold hover:underline active:text-blue-300"
                >
                    {doAction}
                </Link>
            </p>
        </div>
    )
}

export default AuthSwitch