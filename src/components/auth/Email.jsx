export function Email(){
    return(
        <>
            <div>
                <label className="block mb-1">Email</label>
                <input
                    type="text"
                    placeholder="name@gmail.com"
                    className="bg-white placeholder:text-sm placeholder:pl-3 w-full
                            border border-gray-300 outline-none rounded-md py-2 px-2 mb-4
                            transition-all duration-200 hover:shadow-md hover:bg-gray-100 hover:border-blue-600
                             focus:border-blue-600 focus:ring-2 focus:ring-blue-200 focus:shadow-lg"
                />
            </div>
        </>
    )
}

export default Email