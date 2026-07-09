import {useState} from "react";
import {FiEye, FiEyeOff} from "react-icons/fi";

export function EmailPasswordButton(){
    const [showPassword, setShowPassword] = useState(false)

    return (
        <>
            <div>
                <label className="block mb-1">Email</label>
                <input
                    type="text"
                    placeholder="ex: name@gmail.com"
                    className="bg-white placeholder:text-sm w-full
                            border border-gray-300 outline-none rounded-md py-2 px-2 mb-4
                            "
                />
            </div>
            <div>
                <label className="block mb-1">Password</label>
                <div className="relative">
                    <input
                        type={showPassword ? "text" : "password"}
                        placeholder="create a Password"
                        className="bg-white placeholder:text-sm w-full
                            border border-gray-300 outline-none rounded-md py-2 px-2 pr-10 mb-4
                            "
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

export default EmailPasswordButton