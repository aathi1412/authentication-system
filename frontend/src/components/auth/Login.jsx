import {zodResolver} from "@hookform/resolvers/zod";
import axios from "axios";
import {useState} from "react";
import {useForm} from "react-hook-form";
import toast from "react-hot-toast";
import {Link, useNavigate} from "react-router-dom";
import {BASE_PATH_AUTH} from "../../utils/constants";
import {LoginSchema} from "../validations/authSchema";
import AuthSwitch from "./AuthSwitch.jsx";
import Button from "./Button.jsx";
import Email from "./Email.jsx";
import Password from "./Password.jsx";

export function Login(){

    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm({
        resolver: zodResolver(LoginSchema)
    })

    const onSubmit = async (data)=>{
        console.log(data)

        try {
            setLoading(true)
            const response = await toast.promise(
                axios.post(BASE_PATH_AUTH + "/login", data),
                {
                    loading: "signing you in",
                    success: "Welcome back!",
                    error: (err) => err.response?.data?.message || "Something went wrong."
                }
            );

            console.log(response.data);
            navigate("/")
        }
        finally {
            setLoading(false)
        }
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
                            placeholder="Enter your Password"
                            register={register}
                            errors={errors}
                        />
                    </div>

                    <div className="w-full flex justify-between items-center mt-4 mb-6">
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
                    >
                        Sign In
                    </Button>

                    <AuthSwitch
                        doAction="Sign up"
                        path="/register"
                    >
                        Don't have an account?{" "}
                    </AuthSwitch>
                </form>
            </div>
        </>
    )
}

export default Login;