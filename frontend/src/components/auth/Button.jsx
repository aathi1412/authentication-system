export function Button({text}){
    return(
        <div className="flex items-center justify-center my-2">
            <button
                // type="button"
                className="text-white font-bold border bg-[#2f6fed] w-full py-3 rounded-lg hover:bg-blue-700 "
            >
                {text}
            </button>
        </div>
    )
}

export default Button