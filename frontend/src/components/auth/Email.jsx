

export function Email({ register, errors}){
    return(
        <>
            <div className="mb-3">
                <label className="block mb-1">Email</label>
                <input
                    type="email"
                    placeholder="name@gmail.com"
                    {...register("email")}
                    className="bg-white placeholder:text-sm placeholder:pl-3 w-full
                            border border-gray-300 outline-none rounded-md py-2 px-2 mb-1
                            transition-all duration-200 hover:shadow-md hover:bg-gray-100 hover:border-blue-600
                             focus:border-blue-600 focus:ring-2 focus:ring-blue-200 focus:shadow-lg"
                />
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