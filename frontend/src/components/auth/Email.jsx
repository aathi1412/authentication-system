import {HiOutlineMail} from "react-icons/hi";

export function Email({ register, errors}){
    return(
        <>
            <div className="mb-3">
                <label className="block mb-1">Email</label>
                <div className="relative">
                    <HiOutlineMail className="absolute left-3 top-3.5 text-[#a1a1a1] pointer-events-none" />
                    <input
                        type="email"
                        placeholder = "Enter Your Email"
                        {...register("email")}
                        className="bg-white placeholder:text-[14px] pl-10 w-full
                            border border-gray-300 outline-none rounded-md py-2 px-2 mb-1
                            transition-all duration-200 hover:shadow-md hover:bg-gray-100 hover:border-blue-600
                             focus:border-blue-600 focus:ring-2 focus:ring-blue-200 focus:shadow-lg"
                    />
                </div>
                {errors.email &&
                    <p className="mt-2 rounded-md bg-red-50 border border-red-200 px-3 py-2 text-sm text-red-700">
                        {errors.email.message}
                    </p>
                }
            </div>
        </>
    )
}

export default Email