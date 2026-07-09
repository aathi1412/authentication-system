export function EmailPasswordButton(){
    return (
        <>
            <div>
                <label className="block mb-1">Email</label>
                <input
                    type="text"
                    placeholder="ex: name@gmail.com"
                    className="placeholder:text-sm border rounded-md py-2 px-2 mb-4 w-70"
                />
            </div>
            <div>
                <label className="block mb-1">Password</label>
                <input
                    type="password"
                    placeholder="create a Password"
                    className="placeholder:text-sm border rounded-md py-2 px-2 mb-4 w-70"
                />
            </div>

            <button>Sign Up</button>
        </>
    )
}

export default EmailPasswordButton