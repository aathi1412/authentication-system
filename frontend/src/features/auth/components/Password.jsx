import {useState} from "react";
import {FiEye, FiEyeOff} from "react-icons/fi";
import {LuLock} from "react-icons/lu";

export function Password({
                             name = "password",
                             label,
                             placeholder,
                             register,
                             errors
                         }) {
    const [showPassword, setShowPassword] = useState(false)

    return (
        <>
            <div>
                <label className="block mb-1">{label}</label>
                <div className="relative mb-3">
                    <LuLock className="absolute left-3 top-3.5 text-[#a1a1a1] pointer-events-none"/>
                    <input
                        type={showPassword ? "text" : "password"}
                        placeholder={placeholder}
                        {...register(name)}
                        className="bg-white placeholder:text-sm pl-10 w-full
                            border border-gray-300 outline-none rounded-md py-2 px-2 pr-10 mb-1
                            transition-all duration-200 hover:shadow-md hover:bg-gray-100 hover:border-blue-600
                             focus:border-blue-600 focus:ring-2 focus:ring-blue-200 focus:shadow-lg "
                    />
                    <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-3 top-1 translate-y-1.5"
                    >
                        {showPassword ? <FiEye size={20}/> : <FiEyeOff size={20}/>}
                    </button>

                    {errors[name] &&
                        <p className="mt-2 rounded-md bg-red-50 border border-red-200 px-3 py-2 text-sm text-red-700">
                            {errors[name].message}
                        </p>
                    }
                </div>
            </div>
        </>
    )
}

export default Password