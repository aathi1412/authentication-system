import {useState} from "react";
import {useForm} from "react-hook-form";
import {Link} from "react-router-dom";
import AuthSwitch from "./AuthSwitch.jsx";
import Button from "./Button.jsx";
import Email from "./Email.jsx";
import Password from "./Password.jsx";

export function Login(){
    const [loading, setLoading] = useState(false)
    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm()

    const onSubmit = (data)=>{
        setLoading(true)
        console.log(data)
    }
    return(
        <>
            <div className="min-h-screen flex flex-col items-center justify-center ">

                <form noValidate onSubmit={handleSubmit(onSubmit)}
                    className="px-15 py-10 shadow-2xl rounded-3xl">
                    <div>
                        <h2 className="font-bold text-2xl mt-3 ">Welcome back</h2>
                        <p className="text-sm text-gray-500 mt-1">Log in and let's pick up where you left off.</p>
                    </div>
                    <div className="mt-7">
                        <Email
                            register={register}
                            errors={errors}
                        />
                        <Password
                            label="Password"
                            placeholder="create a Password"
                            register={register}
                            errors={errors}
                        />
                    </div>

                    <div className="flex justify-between items-center mb-4">
                        <label className="flex items-center gap-2 cursor-pointer">
                            <input
                                type="checkbox"
                                className="accent-blue-600 cursor-pointer hover:scale-110 transition"
                            />
                            <span className="text-[13px] text-gray-600">
                              Keep me signed in
                            </span>
                        </label>

                        <Link
                            to="/forgot-password"
                            className="text-[13px] font-semibold text-[#3172ed] hover:underline active:text-blue-300"
                        >
                            Forgot Password?
                        </Link>
                    </div>

                    <Button
                        text="Sign in"
                        loading={loading}
                    />

                    <AuthSwitch
                        text="Don't have an account? "
                        doAction="Sign up"
                        path="/register"
                    />
                </form>
            </div>
        </>
    )
}

export default Login;