export function Name(){
    return(
        <div>
            <label htmlFor="username" className="block mb-1">Full Name</label>
            <input
                id="username"
                type="text"
                placeholder="Roronoa Zoro"
                className="bg-white placeholder:text-sm placeholder:pl-3 w-full
                        border border-gray-300 outline-none rounded-md py-2 px-2 mb-4
                         transition-all duration-200 hover:shadow-md hover:bg-gray-100 hover:border-blue-600
                          focus:border-blue-600 focus:ring-2 focus:ring-blue-200 focus:shadow-lg"
            />
        </div>
    )
}

export default Name