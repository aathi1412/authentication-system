import Email from "./Email.jsx";
import Button from "./Button.jsx";
import AuthSwitch from "./AuthSwitch.jsx";

export function ForgotPassword(){
    return(
        <>
            <div className="min-h-screen flex flex-col items-center justify-center ">

                <form className="w-full max-w-lg px-15 py-10 shadow-2xl rounded-3xl">

                    <h2 className="text-2xl font-bold mb-2">Forgot your password?</h2>
                    <p className="text-gray-500 mb-6">Enter your email and we’ll send you a link to reset your password.</p>

                    <Email/>

                    <Button
                        text="Confirm"
                    />

                    <AuthSwitch
                        text="Remember your password? "
                        doAction="Sign in"
                        path="/login"
                    />

                </form>
            </div>
        </>
    )
}

export default ForgotPassword