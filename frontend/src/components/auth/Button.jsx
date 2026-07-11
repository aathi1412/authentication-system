export function Button({text, loading}){
    return(
        <div className="flex items-center justify-center my-2">
            <button
                type="submit"
                disabled={loading}
                className="
                     font-bold
                     border rounded-lg
                     w-full py-3
                     text-white bg-[#2f6fed]
                     hover:bg-blue-700
                     disabled:opacity-60
                     disabled:cursor-not-allowed
                "
            >
                {text}
            </button>
        </div>
    )
}

export default Button