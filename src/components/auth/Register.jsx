import Password from "./Password.jsx";
import TacPopup from "../../utils/TacPopup.jsx";
import Email from "./Email.jsx";
import Name from "./Name.jsx";
import Button from "./Button.jsx";
import AuthSwitch from "./AuthSwitch.jsx";

export function Register(){
    return(
        <>
            <div className="min-h-screen flex flex-col items-center justify-center ">

                <form className="px-15 py-10 shadow-2xl rounded-3xl">
                    <div>
                        <h2 className="font-bold text-2xl m-3 p-2">
                            Create your account
                        </h2>
                    </div>

                    <Name/>
                    <Email/>
                    <Password
                        label="Password"
                        placeholder="create a Password"
                    />

                    <TacPopup/>

                    <Button
                        text="create account"
                    />

                    <AuthSwitch
                        text="Already have an account? "
                        doAction="Sign in"
                        path="/login"
                    />

                </form>
            </div>
        </>
    )
}

export default Register;