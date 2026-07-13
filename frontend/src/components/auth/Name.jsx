import {CiUser} from "react-icons/ci";

export function Name({register, errors}){
    return(
        <div className="mb-3">
            <label htmlFor="username" className="block mb-1">Full Name</label>
            <div className="relative">
                <CiUser className="absolute top-3.5 left-3"/>
                <input
                    id="username"
                    type="text"
                    placeholder="Roronoa Zoro"
                    {...register("name")}
                    className="bg-white placeholder:text-sm pl-10 w-full
                        border border-gray-300 outline-none rounded-md py-2 px-2 mb-1
                         transition-all duration-200 hover:shadow-md hover:bg-gray-100 hover:border-blue-600
                          focus:border-blue-600 focus:ring-2 focus:ring-blue-200 focus:shadow-lg"
                />
            </div>
            {errors.name &&
                <p className="mt-2 rounded-md bg-red-50 border border-red-200 px-3 py-2 text-sm text-red-700">
                    {errors.name.message}
                </p>}
        </div>
    )
}

export default Name