import {Link} from "react-router-dom";

export function AuthSwitch({ text, path, doAction}){
    return(
        <div className="mt-4 text-gray-600 text-[13px] text-center">
            <p>
                {text}
                <Link to={path}>
                    <span className="text-[#3172ed] font-semibold hover:underline active:text-blue-300">
                        {doAction}
                    </span>
                </Link>
            </p>
        </div>
    )
}

export default AuthSwitch