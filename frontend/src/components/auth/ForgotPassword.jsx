import {zodResolver} from "@hookform/resolvers/zod";
import axios from "axios";
import {useForm} from "react-hook-form";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";
import {BASE_PATH_AUTH} from "../../utils/constants";
import {ForgotPasswordSchema} from "../validations/authSchema";
import AuthSwitch from "./AuthSwitch.jsx";
import Button from "./Button.jsx";
import Email from "./Email.jsx";

export function ForgotPassword(){

    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors }
    } = useForm({
        resolver: zodResolver(ForgotPasswordSchema)
    })

    const onSubmit = async (data) => {
        localStorage.setItem("email", data.email);
        await toast.promise(
            axios.post(BASE_PATH_AUTH + "/forgot-password", data),
            {
                loading: "Sending reset link...",
                success: "Reset link sent to your email.",
                error: err => err.response?.data?.error?.message || "Something went wrong."
            }
        );
        reset()
        navigate("/forgot-password/email-sent")
        console.log("sent")
    }

    return(
        <>
            <div className="min-h-screen flex flex-col items-center justify-center ">

                <form noValidate onSubmit={handleSubmit(onSubmit)}
                    className="w-full max-w-lg px-15 py-10 shadow-2xl rounded-3xl">

                    <h2 className="text-2xl font-bold mb-2">Forgot your password?</h2>
                    <p className="text-gray-500 mb-6">Enter your email and we’ll send you a link to reset your password.</p>

                    <Email
                        register={register}
                        errors={errors}
                    />

                    <Button>
                        Confirm
                    </Button>

                    <AuthSwitch
                        doAction="Sign in"
                        path="/login"
                    >
                        Remember your password? {" "}
                    </AuthSwitch>

                </form>
            </div>
        </>
    )
}

export default ForgotPassword