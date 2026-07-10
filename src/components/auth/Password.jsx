import {useState} from "react";
import {FiEye, FiEyeOff} from "react-icons/fi";

export function Password({ label, placeholder }){
    const [showPassword, setShowPassword] = useState(false)

    return (
        <>
            <div>
                <label className="block mb-1">{label}</label>
                <div className="relative">
                    <input
                        type={showPassword ? "text" : "password"}
                        placeholder={placeholder}
                        className="bg-white placeholder:text-sm placeholder:pl-3 w-full
                            border border-gray-300 outline-none rounded-md py-2 px-2 pr-10 mb-4
                            transition-all duration-200 hover:shadow-md hover:bg-gray-100 hover:border-blue-600
                             focus:border-blue-600 focus:ring-2 focus:ring-blue-200 focus:shadow-lg "
                    />
                    <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-3 top-1 translate-y-1.5">{showPassword ? <FiEye size={20}/> : <FiEyeOff size={20}/>}</button>
                </div>
            </div>
        </>
    )
}

export default Password