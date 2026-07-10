import Button from "./Button.jsx";
import AuthSwitch from "./AuthSwitch.jsx";
import Password from "./Password.jsx";

export function ResetPassword(){
    return(
        <>
            <div className="min-h-screen flex items-center justify-center ">

                <form className="w-full max-w-lg px-15 py-10 shadow-2xl rounded-3xl">

                    <h2 className="text-2xl font-bold mb-2">
                        Reset your password?
                    </h2>
                    <p className="text-gray-500 mb-6">
                         Your new password should be different from previously used.
                    </p>

                    <Password
                        label="New Password"
                        placeholder="create a Password"
                    />
                    <Password
                        label="Confirm your new password"
                        placeholder="Confirm your Password"
                    />

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