export function Button({
                       type = 'submit',
                       loading = false,
                       onClick,
                       children
}){
    return(
        <div className="flex items-center justify-center my-2">
            <button
                type={type}
                onClick={onClick}
                disabled={loading}
                className="
                     font-bold
                      rounded-lg
                     w-full py-3
                     text-white bg-[#2f6fed]
                     hover:bg-blue-700
                     transition-colors
                     disabled:opacity-60
                     disabled:cursor-not-allowed
                "
            >
                {children}
            </button>
        </div>
    )
}

export default Button